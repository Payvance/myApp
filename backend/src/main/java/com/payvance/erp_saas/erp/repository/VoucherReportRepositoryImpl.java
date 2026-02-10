package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.dto.VoucherReportDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class VoucherReportRepositoryImpl implements VoucherReportRepositoryCustom {

    @PersistenceContext(unitName = "erp")
    private EntityManager entityManager;

    @org.springframework.beans.factory.annotation.Autowired
    private TallyVoucherTypeRepository voucherTypeRepository;

    @Override
    @SuppressWarnings("unchecked")
    public List<VoucherReportDTO> getVoucherReport(
            Long tenantId,
            String companyId,
            String voucherType,
            String fromDate,
            String toDate,
            String groupBy,
            Map<String, String> filters) {

        // Determine Mode: Inventory Mode vs Voucher Mode
        boolean isInventoryGrouping = List.of("stockItem", "stockGroup", "category").contains(groupBy);
        boolean hasInventoryFilters = filters != null && filters.keySet().stream()
                .anyMatch(k -> k.startsWith("stock") || k.startsWith("category"));
        boolean useInventoryMode = isInventoryGrouping || hasInventoryFilters;

        if (useInventoryMode || "costCenter".equals(groupBy)) {
            // Use existing Single-Query Logic (Turnover/Item Values)
            return executeQuery(tenantId, companyId, voucherType, fromDate, toDate, groupBy, filters, true);
        } else {
            // Voucher Mode: Split Query (Bill Values + Quantities)
            // 1. Fetch Amounts (From Vouchers table only - No join duplication)
            List<VoucherReportDTO> amounts = executeQuery(tenantId, companyId, voucherType, fromDate, toDate, groupBy,
                    filters, false);

            // 2. Fetch Quantities (From Inventory join - Qty only)
            // We reuse the inventory query but we only care about Qty
            // Efficient: We can arguably skip this if Qty is not needed, but let's fetch
            // for completeness
            // Or better: We specifically run a "Qty Only" query helper?
            // To save implementation size, let's reuse executeQuery with a flag?
            // executeQuery is complex. Let's call it with 'inventoryMode=true' but then we
            // get Amount as 'ie.amount'.
            // We want to MERGE: Amount from Step 1, Qty from Step 2.

            List<VoucherReportDTO> quantities = executeQuery(tenantId, companyId, voucherType, fromDate, toDate,
                    groupBy, filters, true);

            // 3. Merge
            Map<String, VoucherReportDTO> map = new java.util.HashMap<>();

            // Populate Amounts
            for (VoucherReportDTO a : amounts) {
                map.put(a.getName(), a);
            }

            // Merge Quantities
            for (VoucherReportDTO q : quantities) {
                if (map.containsKey(q.getName())) {
                    VoucherReportDTO existing = map.get(q.getName());
                    // Update Qty
                    // Note: DTO is immutable-ish? No, usually POJO. Assuming mutable setters or
                    // create new.
                    map.put(q.getName(),
                            new VoucherReportDTO(existing.getName(), q.getQuantity(), existing.getAmount()));
                } else {
                    // Qty exists but Amount 0 (e.g. Free items only voucher?)
                    map.put(q.getName(), new VoucherReportDTO(q.getName(), q.getQuantity(), BigDecimal.ZERO));
                }
            }

            return new ArrayList<>(map.values());
        }
    }

    private List<VoucherReportDTO> executeQuery(
            Long tenantId,
            String companyId,
            String voucherType,
            String fromDate,
            String toDate,
            String groupBy,
            Map<String, String> filters,
            boolean useInventoryTable) {

        // 1. Fetch Hierarchy (Same as before)
        List<com.payvance.erp_saas.erp.entity.TallyVoucherType> allTypes;
        if (companyId != null && !companyId.isEmpty()) {
            allTypes = voucherTypeRepository.findAllByTenantIdAndCompanyId(tenantId, companyId);
        } else {
            allTypes = voucherTypeRepository.findAllByTenantId(tenantId);
        }

        Map<String, List<String>> parentToChildren = new java.util.HashMap<>();
        for (com.payvance.erp_saas.erp.entity.TallyVoucherType t : allTypes) {
            String p = t.getParent();
            if (p != null && !p.isEmpty()) {
                parentToChildren.computeIfAbsent(p, k -> new ArrayList<>()).add(t.getName());
            }
        }

        java.util.Set<String> negationTypes = new java.util.HashSet<>();
        expandTypes(negationTypes, "Credit Note", parentToChildren);
        expandTypes(negationTypes, "Debit Note", parentToChildren);
        if (negationTypes.isEmpty()) {
            negationTypes.add("Credit Note");
            negationTypes.add("Debit Note");
        }

        StringBuilder sql = new StringBuilder();
        String groupColumn = "";

        // Logic Switch:
        // If useInventoryTable = true (Inventory Mode): Use 'ie.amount' (Item Value) +
        // 'ie.billed_qty'
        // If useInventoryTable = false (Voucher Mode): Use 'v.amount' (Bill Value), Qty
        // = 0 (Calculated later via merge)

        String qtySql = useInventoryTable
                ? "SUM(CASE WHEN v.voucher_type IN (:negationTypes) THEN  CAST(COALESCE(NULLIF(REGEXP_REPLACE(ie.billed_qty, '[^0-9.-]', ''), ''), '0') AS DECIMAL(19,4)) ELSE CAST(COALESCE(NULLIF(REGEXP_REPLACE(ie.billed_qty, '[^0-9.-]', ''), ''), '0') AS DECIMAL(19,4)) END)"
                : "0";

        String amountSql = useInventoryTable
                ? "SUM(CASE WHEN v.voucher_type IN (:negationTypes) THEN  (CASE WHEN v.is_invoice = 0 OR v.is_invoice IS NULL THEN v.amount ELSE ie.amount END) ELSE (CASE WHEN v.is_invoice = 0 OR v.is_invoice IS NULL THEN v.amount ELSE ie.amount END) END)"
                : "SUM(CASE WHEN v.voucher_type IN (:negationTypes) THEN  v.amount ELSE v.amount END)";

        switch (groupBy) {
            case "ledger":
                groupColumn = "v.party_ledger_name";
                sql.append("SELECT v.party_ledger_name, ").append(qtySql).append(", ").append(amountSql)
                        .append(" FROM tally_vouchers v ");
                if (useInventoryTable)
                    sql.append("LEFT JOIN tally_inventory_entries ie ON ie.voucher_id = v.id ");
                break;
            case "ledgerGroup":
                groupColumn = "g.name";
                sql.append("SELECT g.name, ").append(qtySql).append(", ").append(amountSql)
                        .append(" FROM tally_vouchers v ");
                sql.append("JOIN tally_ledgers l ON l.name = v.party_ledger_name ");
                sql.append("JOIN tally_groups g ON g.name = l.group_name ");
                if (useInventoryTable)
                    sql.append("LEFT JOIN tally_inventory_entries ie ON ie.voucher_id = v.id ");
                break;
            case "stockItem":
                groupColumn = "ie.stock_item_name";
                sql.append("SELECT ie.stock_item_name, ").append(qtySql).append(", ").append(amountSql)
                        .append(" FROM tally_vouchers v ");
                sql.append("JOIN tally_inventory_entries ie ON ie.voucher_id = v.id ");
                break;
            case "stockGroup":
                groupColumn = "si.stock_group_name";
                sql.append("SELECT si.stock_group_name, ").append(qtySql).append(", ").append(amountSql)
                        .append(" FROM tally_vouchers v ");
                sql.append("JOIN tally_inventory_entries ie ON ie.voucher_id = v.id ");
                sql.append("JOIN tally_stock_items si ON si.name = ie.stock_item_name ");
                break;
            case "category":
                groupColumn = "si.category_name";
                sql.append("SELECT si.category_name, ").append(qtySql).append(", ").append(amountSql)
                        .append(" FROM tally_vouchers v ");
                sql.append("JOIN tally_inventory_entries ie ON ie.voucher_id = v.id ");
                sql.append("JOIN tally_stock_items si ON si.name = ie.stock_item_name ");
                break;
            case "costCenter":
                // Cost Center always joins ledger entries
                groupColumn = "cc.name";
                sql.append("SELECT cc.name, CAST(COUNT(*) AS DECIMAL(19,4)), SUM(le.amount) FROM tally_vouchers v ");
                sql.append("JOIN tally_ledger_entries le ON le.voucher_id = v.id ");
                sql.append("JOIN tally_cost_centres cc ON cc.name = le.ledger_name ");
                break;
            case "month":
                groupColumn = "txn_month";
                sql.append("SELECT DATE_FORMAT(v.date, '%Y-%m') AS txn_month, ").append(qtySql).append(", ")
                        .append(amountSql).append(" FROM tally_vouchers v ");
                if (useInventoryTable)
                    sql.append("LEFT JOIN tally_inventory_entries ie ON ie.voucher_id = v.id ");
                break;
            case "voucher":
                groupColumn = "v.id";
                sql.append(
                        "SELECT CONCAT(v.voucher_number, '|', v.id, '|', DATE_FORMAT(v.date, '%Y-%m-%d'), '|', v.voucher_type), ")
                        .append(qtySql).append(", ").append(amountSql).append(" FROM tally_vouchers v ");
                if (useInventoryTable)
                    sql.append("LEFT JOIN tally_inventory_entries ie ON ie.voucher_id = v.id ");
                break;
            case "voucherType":
                groupColumn = "v.voucher_type";
                sql.append("SELECT v.voucher_type, ").append(qtySql).append(", ").append(amountSql)
                        .append(" FROM tally_vouchers v ");
                if (useInventoryTable)
                    sql.append("LEFT JOIN tally_inventory_entries ie ON ie.voucher_id = v.id ");
                break;
            default:
                throw new IllegalArgumentException("Invalid groupBy dimension: " + groupBy);
        }

        // Base filters
        sql.append(" WHERE v.tenant_id = :tenantId ");
        if (companyId != null && !companyId.isEmpty()) {
            sql.append(" AND v.company_id = :companyId ");
        }

        java.util.Set<String> filterTypes = null;
        if (voucherType != null && !voucherType.isEmpty()) {
            filterTypes = new java.util.HashSet<>();
            expandTypes(filterTypes, voucherType, parentToChildren);
            if ("Sales".equalsIgnoreCase(voucherType)) {
                expandTypes(filterTypes, "Credit Note", parentToChildren);
            } else if ("Purchase".equalsIgnoreCase(voucherType)) {
                expandTypes(filterTypes, "Debit Note", parentToChildren);
            }
            sql.append(" AND v.voucher_type IN (:filterTypes) ");
        }

        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append(" AND v.date >= :fromDate ");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append(" AND v.date <= :toDate ");
        }

        // Drill-down filters (Same logic, mostly)
        if (filters != null) {
            if (filters.containsKey("ledgerName"))
                sql.append(" AND v.party_ledger_name = :ledgerName ");
            if (filters.containsKey("ledgerGroupName")) {
                if (!groupBy.equals("ledgerGroup")) {
                    sql.append(
                            " AND EXISTS (SELECT 1 FROM tally_ledgers fl JOIN tally_groups fg ON fg.name = fl.group_name WHERE fl.name = v.party_ledger_name AND fg.name = :ledgerGroupName) ");
                } else {
                    sql.append(" AND g.name = :ledgerGroupName ");
                }
            }
            // For stock filters, even in "Voucher Mode" (Amount), we can filter using
            // EXISTS logic without joining.
            if (filters.containsKey("stockGroupName")) {
                sql.append(
                        " AND EXISTS (SELECT 1 FROM tally_inventory_entries fie JOIN tally_stock_items fsi ON fsi.name = fie.stock_item_name WHERE fie.voucher_id = v.id AND fsi.stock_group_name = :stockGroupName) ");
            }
            if (filters.containsKey("stockItemName")) {
                sql.append(
                        " AND EXISTS (SELECT 1 FROM tally_inventory_entries fie WHERE fie.voucher_id = v.id AND fie.stock_item_name = :stockItemName) ");
            }
            if (filters.containsKey("categoryName")) {
                sql.append(
                        " AND EXISTS (SELECT 1 FROM tally_inventory_entries fie JOIN tally_stock_items fsi ON fsi.name = fie.stock_item_name WHERE fie.voucher_id = v.id AND fsi.category_name = :categoryName) ");
            }
            if (filters.containsKey("costCenterName")) {
                sql.append(
                        " AND EXISTS (SELECT 1 FROM tally_ledger_entries fle JOIN tally_cost_centres fcc ON fcc.name = fle.ledger_name WHERE fle.voucher_id = v.id AND fcc.name = :costCenterName) ");
            }
            if (filters.containsKey("month")) {
                sql.append(" AND DATE_FORMAT(v.date, '%Y-%m') = :month ");
            }
        }

        sql.append(" GROUP BY ").append(groupColumn);
        // Ensure consistent sorting
        sql.append(" ORDER BY ").append(groupColumn);

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("tenantId", tenantId);
        query.setParameter("negationTypes", negationTypes); // Always set

        if (companyId != null && !companyId.isEmpty())
            query.setParameter("companyId", companyId);

        if (filterTypes != null) {
            query.setParameter("filterTypes", filterTypes);
        }

        if (fromDate != null && !fromDate.isEmpty())
            query.setParameter("fromDate", fromDate);
        if (toDate != null && !toDate.isEmpty())
            query.setParameter("toDate", toDate);

        if (filters != null) {
            if (filters.containsKey("ledgerName"))
                query.setParameter("ledgerName", filters.get("ledgerName"));
            if (filters.containsKey("ledgerGroupName"))
                query.setParameter("ledgerGroupName", filters.get("ledgerGroupName"));
            if (filters.containsKey("stockGroupName"))
                query.setParameter("stockGroupName", filters.get("stockGroupName"));
            if (filters.containsKey("stockItemName"))
                query.setParameter("stockItemName", filters.get("stockItemName"));
            if (filters.containsKey("categoryName"))
                query.setParameter("categoryName", filters.get("categoryName"));
            if (filters.containsKey("costCenterName"))
                query.setParameter("costCenterName", filters.get("costCenterName"));
            if (filters.containsKey("month"))
                query.setParameter("month", filters.get("month"));
        }

        List<Object[]> results = query.getResultList();
        List<VoucherReportDTO> dtos = new ArrayList<>();
        for (Object[] row : results) {
            String name = (String) row[0];
            BigDecimal quantity = toBigDecimal(row[1]);
            BigDecimal amount = toBigDecimal(row[2]);
            dtos.add(new VoucherReportDTO(name, quantity, amount.abs()));

        }
        return dtos;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null)
            return BigDecimal.ZERO;
        if (value instanceof BigDecimal)
            return (BigDecimal) value;
        if (value instanceof Number)
            return new BigDecimal(value.toString());
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO; // Handle partial/invalid numbers gracefully
        }
    }

    private void expandTypes(java.util.Set<String> set, String root, Map<String, List<String>> tree) {
        if (!set.add(root)) {
            return; // Already added
        }
        if (tree.containsKey(root)) {
            List<String> children = tree.get(root);
            for (String c : children) {
                expandTypes(set, c, tree);
            }
        }
    }
}

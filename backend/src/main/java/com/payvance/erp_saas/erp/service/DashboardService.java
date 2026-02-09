package com.payvance.erp_saas.erp.service;

import com.payvance.erp_saas.erp.dto.DashboardBannerDTO;
import com.payvance.erp_saas.erp.dto.DashboardHeaderDTO;
import com.payvance.erp_saas.erp.dto.DashboardStatsDTO;
import com.payvance.erp_saas.erp.dto.VoucherTileDTO;
import com.payvance.erp_saas.erp.entity.TallyVoucherType;
import com.payvance.erp_saas.erp.entity.SyncState;
import com.payvance.erp_saas.erp.repository.SyncStateRepository;
import com.payvance.erp_saas.erp.repository.TallyCompanyRepository;
import com.payvance.erp_saas.erp.repository.TallyLedgerRepository;
import com.payvance.erp_saas.erp.repository.TallyVoucherTypeRepository;
import com.payvance.erp_saas.erp.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TallyLedgerRepository ledgerRepository;
    private final TallyVoucherTypeRepository voucherTypeRepository;
    private final TallyCompanyRepository companyRepository;
    private final SyncStateRepository syncStateRepository;
    private final VoucherRepository voucherRepository;

    public DashboardHeaderDTO getHeader(Long tenantId, String companyId) {
        DashboardHeaderDTO header = new DashboardHeaderDTO();

        companyRepository.findByTenantIdAndGuid(tenantId, companyId).ifPresent(company -> {
            header.setCompanyName(company.getName());
            if (company.getBooksFrom() != null && !company.getBooksFrom().isEmpty()) {
                try {
                    // Tally often sends dd-MMM-yyyy or similar formats.
                    // Just in case, simplistic parsing or use a robust helper if available.
                    // Assuming standard format or attempting format.
                    // For now, let's try direct parse or TallyXmlParser helper if accessible,
                    // but TallyXmlParser is for XML. TallyCompany stores strings.
                    // Let's rely on standard parsing for "yyyy-MM-dd" or "yyyyMMdd" or try
                    // robustly.
                    // Given previous code history seeing formats like "d-MMM-yyyy"

                    // We can use a DateTimeFormatter
                    java.time.format.DateTimeFormatter formatter = new java.time.format.DateTimeFormatterBuilder()
                            .parseCaseInsensitive()
                            .appendPattern("[d-MMM-yyyy][yyyyMMdd][yyyy-MM-dd]")
                            .toFormatter();
                    header.setBooksFrom(LocalDate.parse(company.getBooksFrom(), formatter));
                } catch (Exception e) {
                    System.err.println("Failed to parse booksFrom: " + company.getBooksFrom());
                    // Fallback to reasonable default or null
                }
            }
        });

        // Default to current date if not specified
        header.setFromDate(LocalDate.now().withDayOfYear(1)); // Start of year (Simplification)
        header.setToDate(LocalDate.now());

        // Use SyncState for lastSyncAt
        if (companyId != null) {
            syncStateRepository.findByTenantIdAndCompanyId(tenantId, companyId).ifPresent(state -> {
                header.setLastSyncAt(state.getLastSyncTime());
            });
        }

        if (header.getLastSyncAt() == null) {
            header.setLastSyncAt(LocalDateTime.now()); // Fallback
        }

        return header;
    }

    public DashboardBannerDTO getBanner() {
        DashboardBannerDTO banner = new DashboardBannerDTO();
        banner.setTitle("Premium Features");
        banner.setSubtitle("Unlock advanced analytics and reporting tools");
        banner.setCtaText("Upgrade Now");
        banner.setDiscountBadge("50% OFF");
        banner.setEnabled(true);
        return banner;
    }

    public DashboardStatsDTO getStats(Long tenantId, String companyId) {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalReceivables(getSum(tenantId, companyId, "Sundry Debtors").abs());
        stats.setTotalPayables(getSum(tenantId, companyId, "Sundry Creditors").abs());

        // Derived from simple logic for now, or could query Vouchers similarly
        return stats;
    }

    private BigDecimal getSum(Long tenantId, String companyId, String groupName) {
        BigDecimal sum = ledgerRepository.sumClosingBalanceByRootGroup(tenantId, companyId, groupName);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    public List<VoucherTileDTO> getVoucherTiles(Long tenantId, String companyId, String category, String fromDate,
            String toDate) {
        // 1. Fetch all Voucher Types
        List<TallyVoucherType> allTypes;
        if (companyId != null && !companyId.isEmpty()) {
            allTypes = voucherTypeRepository.findAllByTenantIdAndCompanyId(tenantId, companyId);
        } else {
            allTypes = voucherTypeRepository.findAllByTenantId(tenantId);
        }

        Map<String, TallyVoucherType> allTypesMap = new HashMap<>();
        for (TallyVoucherType t : allTypes) {
            allTypesMap.put(t.getName(), t);
        }

        // 2. Fetch Voucher Sums for the period
        Map<String, BigDecimal> voucherSums = new HashMap<>();
        List<Object[]> results;

        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();
        try {
            if (fromDate != null)
                start = LocalDate.parse(fromDate);
            if (toDate != null)
                end = LocalDate.parse(toDate);
        } catch (Exception e) {
            System.err.println("Dashboard: Failed to parse dates " + fromDate + " - " + toDate);
        }

        if (companyId != null && !companyId.isEmpty()) {
            results = voucherRepository.getVoucherSumsByDateRange(tenantId, companyId, start, end);
        } else {
            results = voucherRepository.getVoucherSumsByDateRange(tenantId, start, end);
        }

        for (Object[] row : results) {
            String vType = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            voucherSums.put(vType, amount != null ? amount : BigDecimal.ZERO);
        }

        // 3. Build Hierarchy
        Map<String, VoucherTileDTO> typeMap = new HashMap<>();
        List<VoucherTileDTO> roots = new ArrayList<>();

        // create DTOs for all filtered types (belonging to category)
        for (TallyVoucherType type : allTypes) {
            String root = findSystemRoot(type, allTypesMap);
            boolean matched = isCategoryMatchWithRoot(root, category);

            if (matched) {
                VoucherTileDTO dto = new VoucherTileDTO();
                dto.setName(type.getName());
                dto.setAmount(BigDecimal.ZERO); // Will update later
                dto.setChildren(new ArrayList<>());
                typeMap.put(type.getName(), dto);
            }
        }

        // Assign structure
        for (TallyVoucherType type : allTypes) {
            if (!typeMap.containsKey(type.getName()))
                continue;

            VoucherTileDTO dto = typeMap.get(type.getName());
            String parentName = type.getParent();

            // If Type has a parent AND that parent is ALSO in our map (same category), add
            // as child
            if (parentName != null && !parentName.isEmpty() && !parentName.equals(type.getName())
                    && typeMap.containsKey(parentName)) {
                VoucherTileDTO parentDto = typeMap.get(parentName);
                parentDto.getChildren().add(dto);
                parentDto.setHasChildren(true);
            } else {
                // Determine if it's a "Top Level" for this view
                // It is a root if:
                // 1. Parent is null/empty
                // 2. Parent == Name
                // 3. Parent exists but NOT in the same category (Edge case, usually shouldn't
                // happen with standard Tally types)
                roots.add(dto);
            }
        }

        // 4. Update Amounts (Recursive)
        for (VoucherTileDTO root : roots) {
            calculateTotal(root, voucherSums);
        }

        // 5. Apply Adjustment: Sales = Sales - Credit Note; Purchase = Purchase - Debit
        // Note
        VoucherTileDTO sales = null;
        VoucherTileDTO creditNote = null;
        VoucherTileDTO purchase = null;
        VoucherTileDTO debitNote = null;

        for (VoucherTileDTO root : roots) {
            if ("Sales".equalsIgnoreCase(root.getName()))
                sales = root;
            if ("Credit Note".equalsIgnoreCase(root.getName()))
                creditNote = root;
            if ("Purchase".equalsIgnoreCase(root.getName()))
                purchase = root;
            if ("Debit Note".equalsIgnoreCase(root.getName()))
                debitNote = root;
        }

        if (sales != null && creditNote != null) {
            // Subtract Credit Note from Sales
            BigDecimal netSales = sales.getAmount().subtract(creditNote.getAmount());
            // Ensure it doesn't go negative if that's weird, but net sales can be negative
            // conceptually (returns > sales)
            sales.setAmount(netSales);
        }

        if (purchase != null && debitNote != null) {
            // Subtract Debit Note from Purchase
            BigDecimal netPurchase = purchase.getAmount().subtract(debitNote.getAmount());
            purchase.setAmount(netPurchase);
        }

        return roots;
    }

    private BigDecimal calculateTotal(VoucherTileDTO node, Map<String, BigDecimal> sums) {
        BigDecimal total = sums.getOrDefault(node.getName(), BigDecimal.ZERO);
        for (VoucherTileDTO child : node.getChildren()) {
            total = total.add(calculateTotal(child, sums));
        }

        // Determine type based on whether total is negative (DR) or positive (CR)
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            node.setType("DR");
        } else {
            node.setType("CR");
        }

        // Store absolute value for display
        node.setAmount(total.abs());
        return total;
    }

    private boolean isCategoryMatchWithRoot(String rootParent, String category) {
        switch (category) {
            case "Accounting":
                return List.of("Contra", "Journal", "Payment", "Receipt", "Reversing Journal", "Memorandum", "Sales",
                        "Purchase", "Credit Note", "Debit Note").contains(rootParent);
            case "Inventory":
                return List.of("Material In", "Material Out", "Physical Stock", "Stock Journal", "Rejections In",
                        "Rejections Out", "Receipt Note", "Delivery Note").contains(rootParent);
            case "Payroll":
                return List.of("Attendance", "Payroll").contains(rootParent);
            case "Order Voucher":
                return List.of("Sales Order", "Purchase Order", "Job Work In Order", "Job Work Out Order")
                        .contains(rootParent);
            default:
                return false;
        }
    }

    private String findSystemRoot(TallyVoucherType type, Map<String, TallyVoucherType> allTypesMap) {
        String name = type.getName();
        String parent = type.getParent();

        if (parent == null || parent.isEmpty() || parent.equals(name)) {
            return name;
        }

        // Traverse up to 5 levels to avoid infinite loops if data is corrupted
        for (int i = 0; i < 5; i++) {
            if (parent == null || parent.isEmpty() || parent.equals(name)) {
                break;
            }
            TallyVoucherType parentType = allTypesMap.get(parent);
            if (parentType == null) {
                // We reached a parent that is not in our list, return current parent as root
                return parent;
            }
            name = parentType.getName();
            parent = parentType.getParent();
        }
        return parent != null && !parent.isEmpty() ? parent : name;
    }
}

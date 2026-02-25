package com.payvance.erp_saas.erp.service;

import com.payvance.erp_saas.erp.dto.*;
import com.payvance.erp_saas.erp.entity.Voucher;
import com.payvance.erp_saas.erp.entity.InventoryEntry;
import com.payvance.erp_saas.erp.entity.LedgerEntry;
import com.payvance.erp_saas.erp.entity.TallyBatchAllocation;
import com.payvance.erp_saas.erp.repository.VoucherRepository;
import com.payvance.erp_saas.erp.repository.VoucherReportRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "erpTransactionManager", readOnly = true)
public class ReportService {

    private final VoucherReportRepositoryCustom voucherReportRepository;
    private final VoucherRepository voucherRepository;
    private final com.payvance.erp_saas.erp.repository.TallyLedgerRepository tallyLedgerRepository;
    private final com.payvance.erp_saas.erp.repository.ReportDataRepository reportDataRepository;

    public List<VoucherReportDTO> getVoucherReport(
            Long tenantId,
            String companyId,
            String voucherType,
            String fromDate,
            String toDate,
            String groupBy,
            boolean isGross,
            boolean isReturn,
            Map<String, String> filters) {
        return voucherReportRepository.getVoucherReport(tenantId, companyId, voucherType, fromDate, toDate, groupBy,
                isGross, isReturn, filters);
    }

    public String getSavedReport(Long tenantId, String companyId, String reportName) {
        return reportDataRepository.findByTenantIdAndCompanyIdAndReportName(tenantId, companyId, reportName)
                .map(com.payvance.erp_saas.erp.entity.ReportData::getPayload)
                .orElse("{}");
    }

    public List<VoucherReportDTO> getTopLedgers(Long tenantId, String companyId, String type, int limit) {
        if ("sales".equalsIgnoreCase(type) || "purchase".equalsIgnoreCase(type)) {
            String vt = "sales".equalsIgnoreCase(type) ? "Sales" : "Purchase";
            List<VoucherReportDTO> reports = getVoucherReport(tenantId, companyId, vt, null, null, "ledger", true, true,
                    null);
            return reports.stream()
                    .sorted((a, b) -> b.getAmount().abs().compareTo(a.getAmount().abs()))
                    .limit(limit)
                    .collect(Collectors.toList());
        } else if ("debtors".equalsIgnoreCase(type) || "creditors".equalsIgnoreCase(type)) {
            String rootGroup = "debtors".equalsIgnoreCase(type) ? "Sundry Debtors" : "Sundry Creditors";
            org.springframework.data.domain.Page<com.payvance.erp_saas.erp.entity.TallyLedger> page = tallyLedgerRepository
                    .findTopLedgersByRootGroup(tenantId, companyId, rootGroup,
                            org.springframework.data.domain.PageRequest.of(0, limit));
            return page.getContent().stream()
                    .map(l -> new VoucherReportDTO(l.getName(), java.math.BigDecimal.ZERO, l.getClosingBalance()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public List<VoucherReportDTO> getTopItems(Long tenantId, String companyId, String type, int limit) {
        List<VoucherReportDTO> reports = getVoucherReport(tenantId, companyId, "Sales", null, null, "stockItem", false,
                true, null);
        if ("highest".equalsIgnoreCase(type)) {
            return reports.stream()
                    .sorted((a, b) -> b.getQuantity().compareTo(a.getQuantity()))
                    .limit(limit)
                    .collect(Collectors.toList());
        } else if ("slowest".equalsIgnoreCase(type)) {
            return reports.stream()
                    .filter(r -> r.getQuantity().compareTo(java.math.BigDecimal.ZERO) > 0)
                    .sorted((a, b) -> a.getQuantity().compareTo(b.getQuantity()))
                    .limit(limit)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public VoucherDetailDTO getVoucherDetail(Long voucherId, Long tenantId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        // Verify tenant access
        if (!voucher.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Unauthorized access to voucher");
        }

        VoucherDetailDTO dto = new VoucherDetailDTO();
        dto.setId(voucher.getId());
        dto.setGuid(voucher.getGuid());
        dto.setVoucherNumber(voucher.getVoucherNumber());
        dto.setVoucherType(voucher.getVoucherType());
        dto.setDate(voucher.getDate());
        dto.setPartyLedgerName(voucher.getPartyLedgerName());
        dto.setTotalAmount(voucher.getAmount());
        dto.setIsInvoice(voucher.getIsInvoice());

        dto.setNarration(voucher.getNarration());
        dto.setDeliveryNotes(voucher.getDeliveryNotes());
        dto.setPaymentTerms(voucher.getPaymentTerms());
        dto.setConsigneeName(voucher.getConsigneeName());
        dto.setConsigneeAddress(voucher.getConsigneeAddress());
        dto.setBuyerAddress(voucher.getBuyerAddress()); // New
        dto.setInvoiceTotal(voucher.getInvoiceTotal()); // Gross Total for display

        // E-Invoice
        dto.setIrn(voucher.getIrn());
        dto.setAckNo(voucher.getAckNo());
        dto.setIrnAckDate(voucher.getIrnAckDate());
        dto.setIrnQrCode(voucher.getIrnQrCode());

        // E-Way Bill & Transport
        dto.setVehicleNo(voucher.getVehicleNo());
        dto.setTransportMode(voucher.getTransportMode());
        dto.setTransportDistance(voucher.getTransportDistance());
        dto.setEwayBillNo(voucher.getEwayBillNo());
        dto.setEwayBillValidUpto(voucher.getEwayBillValidUpto());

        // Address / Dispatch
        dto.setDispatchName(voucher.getDispatchName());
        dto.setDispatchPlace(voucher.getDispatchPlace());
        dto.setDispatchState(voucher.getDispatchState());
        dto.setDispatchPin(voucher.getDispatchPin());
        dto.setShipPlace(voucher.getShipPlace());

        // Party Details
        dto.setPartyGst(voucher.getPartyGst());
        dto.setPartyMailingName(voucher.getPartyMailingName());
        dto.setPartyPinCode(voucher.getPartyPinCode());
        dto.setGstRegistrationType(voucher.getGstRegistrationType());
        dto.setPlaceOfSupply(voucher.getPlaceOfSupply());
        dto.setBasicBuyerName(voucher.getBasicBuyerName());

        // Company Details
        dto.setCmpGst(voucher.getCmpGst());
        dto.setCmpState(voucher.getCmpState());
        dto.setCmpRegType(voucher.getCmpRegType());

        dto.setShippedBy(voucher.getShippedBy());
        dto.setDestinationCountry(voucher.getDestinationCountry());
        dto.setPlaceOfReceipt(voucher.getPlaceOfReceipt());
        dto.setShipDocumentNo(voucher.getShipDocumentNo());
        dto.setPortOfLoading(voucher.getPortOfLoading());
        dto.setPortOfDischarge(voucher.getPortOfDischarge());
        dto.setFinalDestination(voucher.getFinalDestination());
        dto.setOrderRef(voucher.getOrderRef());
        dto.setShipVesselNo(voucher.getShipVesselNo());
        dto.setBuyersSalesTaxNo(voucher.getBuyersSalesTaxNo());
        dto.setDueDateOfPayment(voucher.getDueDateOfPayment());
        dto.setSerialNumInPla(voucher.getSerialNumInPla());
        dto.setDateTimeOfInvoice(voucher.getDateTimeOfInvoice());
        dto.setDateTimeOfRemoval(voucher.getDateTimeOfRemoval());
        dto.setMfgrAddressType(voucher.getMfgrAddressType());
        dto.setBillOfLadingNo(voucher.getBillOfLadingNo());
        dto.setBillOfLadingDate(voucher.getBillOfLadingDate());

        // Additional Addresses
        dto.setBillPlace(voucher.getBillPlace());

        // Financial Totals
        dto.setTaxableAmount(voucher.getTaxableAmount());
        dto.setCgstAmount(voucher.getCgstAmount());
        dto.setSgstAmount(voucher.getSgstAmount());
        dto.setIgstAmount(voucher.getIgstAmount());
        dto.setRoundOffAmount(voucher.getRoundOffAmount());

        // Business Flags
        dto.setIsCancelled(voucher.getIsCancelled());
        dto.setIsOptional(voucher.getIsOptional());
        dto.setIsDeletedRetained(voucher.getIsDeletedRetained());
        dto.setPersistedView(voucher.getPersistedView());

        // Map inventory entries
        if (voucher.getInventoryEntries() != null) {
            List<VoucherInventoryDetailDTO> inventoryDtos = voucher.getInventoryEntries().stream()
                    .map(this::mapInventoryEntry)
                    .collect(Collectors.toList());
            dto.setInventoryEntries(inventoryDtos);
        }

        // Map ledger entries
        if (voucher.getLedgerEntries() != null) {
            List<VoucherLedgerDetailDTO> ledgerDtos = voucher.getLedgerEntries().stream()
                    .map(this::mapLedgerEntry)
                    .collect(Collectors.toList());
            dto.setLedgerEntries(ledgerDtos);
        }

        if (voucher.getOrders() != null) {
            List<VoucherOrderDetailDTO> orderDtos = voucher.getOrders().stream()
                    .map(this::mapOrderEntry)
                    .collect(Collectors.toList());
            dto.setOrders(orderDtos);
        }

        // Map E-way Bill details
        if (voucher.getEwayBillDetails() != null) {
            List<VoucherEwayBillDTO> ewayDtos = voucher.getEwayBillDetails().stream()
                    .map(this::mapEwayBillEntry)
                    .collect(Collectors.toList());
            dto.setEwayBillDetails(ewayDtos);
        }
        return dto;
    }

    private VoucherInventoryDetailDTO mapInventoryEntry(InventoryEntry entry) {
        VoucherInventoryDetailDTO dto = new VoucherInventoryDetailDTO();
        dto.setStockItemName(entry.getStockItemName());
        dto.setActualQty(entry.getActualQty());
        dto.setBilledQty(entry.getBilledQty());
        dto.setRate(entry.getRate());
        dto.setAmount(entry.getAmount());
        dto.setDiscount(entry.getDiscount());
        dto.setIgstRate(entry.getIgstRate());
        dto.setCgstRate(entry.getCgstRate());
        dto.setSgstRate(entry.getSgstRate());
        dto.setCessRate(entry.getCessRate());
        dto.setHsnName(entry.getHsnName());
        dto.setTypeOfSupply(entry.getTypeOfSupply());
        dto.setGstAssblValue(entry.getGstAssblValue());

        // GST & UOM
        dto.setHsnCode(entry.getHsnCode());
        dto.setGstRate(entry.getGstRate());
        dto.setUom(entry.getUom());
        dto.setCgstAmount(entry.getCgstAmount());
        dto.setSgstAmount(entry.getSgstAmount());
        dto.setIgstAmount(entry.getIgstAmount());

        // Map batch allocations
        if (entry.getBatchAllocations() != null) {
            List<BatchAllocationDetailDTO> batchDtos = entry.getBatchAllocations().stream()
                    .map(this::mapBatchAllocation)
                    .collect(Collectors.toList());
            dto.setBatchAllocations(batchDtos);
        }

        return dto;
    }

    private BatchAllocationDetailDTO mapBatchAllocation(TallyBatchAllocation batch) {
        return new BatchAllocationDetailDTO(
                batch.getGodownName(),
                batch.getBatchName(),
                batch.getActualQty(),
                batch.getBilledQty(),
                batch.getRate(),
                batch.getAmount());
    }

    private VoucherLedgerDetailDTO mapLedgerEntry(LedgerEntry entry) {
        VoucherLedgerDetailDTO dto = new VoucherLedgerDetailDTO();
        dto.setLedgerName(entry.getLedgerName());
        dto.setAmount(entry.getAmount());
        dto.setIsDebit(entry.getIsDebit());
        dto.setIsPartyLedger(entry.getIsPartyLedger());

        // Classification
        dto.setLedgerType(entry.getLedgerType());
        dto.setGstDutyHead(entry.getGstDutyHead());
        dto.setGstClass(entry.getGstNature()); // Mapping gstNature to gstClass in DTO or use specialized field
        dto.setCostCenterName(entry.getCostCenterName());

        return dto;
    }

    private VoucherOrderDetailDTO mapOrderEntry(com.payvance.erp_saas.erp.entity.VoucherOrder order) {
        VoucherOrderDetailDTO dto = new VoucherOrderDetailDTO();
        dto.setBasicPurchaseOrderNo(order.getBasicPurchaseOrderNo());
        dto.setBasicOrderDate(order.getBasicOrderDate());
        return dto;
    }

    private VoucherEwayBillDTO mapEwayBillEntry(com.payvance.erp_saas.erp.entity.EwayBillDetails eway) {
        VoucherEwayBillDTO dto = new VoucherEwayBillDTO();
        dto.setBillNumber(eway.getBillNumber());
        dto.setBillDate(eway.getBillDate());
        dto.setDocumentType(eway.getDocumentType());
        dto.setSubType(eway.getSubType());
        dto.setConsignorName(eway.getConsignorName());
        dto.setConsignorPlace(eway.getConsignorPlace());
        dto.setConsignorPincode(eway.getConsignorPincode());
        dto.setConsignorAddress(eway.getConsignorAddress());
        dto.setConsigneeName(eway.getConsigneeName());
        dto.setConsigneePlace(eway.getConsigneePlace());
        dto.setConsigneePincode(eway.getConsigneePincode());
        dto.setConsigneeAddress(eway.getConsigneeAddress());
        dto.setShippedFromState(eway.getShippedFromState());
        dto.setShippedToState(eway.getShippedToState());
        dto.setIrpSource(eway.getIrpSource());
        dto.setVehicleNumber(eway.getVehicleNumber());
        dto.setTransportMode(eway.getTransportMode());
        dto.setDistance(eway.getDistance());
        dto.setValidUpto(eway.getValidUpto());
        dto.setCancelDate(eway.getCancelDate());
        dto.setCancelReason(eway.getCancelReason());
        return dto;
    }
}

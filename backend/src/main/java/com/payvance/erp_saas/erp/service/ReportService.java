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

    public List<VoucherReportDTO> getVoucherReport(
            Long tenantId,
            String companyId,
            String voucherType,
            String fromDate,
            String toDate,
            String groupBy,
            Map<String, String> filters) {
        return voucherReportRepository.getVoucherReport(tenantId, companyId, voucherType, fromDate, toDate, groupBy,
                filters);
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

        // Additional Addresses
        dto.setBillPlace(voucher.getBillPlace());

        // Financial Totals
        dto.setTaxableAmount(voucher.getTaxableAmount());
        dto.setCgstAmount(voucher.getCgstAmount());
        dto.setSgstAmount(voucher.getSgstAmount());
        dto.setIgstAmount(voucher.getIgstAmount());
        dto.setRoundOffAmount(voucher.getRoundOffAmount());

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

        return dto;
    }

    private VoucherInventoryDetailDTO mapInventoryEntry(InventoryEntry entry) {
        VoucherInventoryDetailDTO dto = new VoucherInventoryDetailDTO();
        dto.setStockItemName(entry.getStockItemName());
        dto.setActualQty(entry.getActualQty());
        dto.setBilledQty(entry.getBilledQty());
        dto.setRate(entry.getRate());
        dto.setAmount(entry.getAmount());

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
}

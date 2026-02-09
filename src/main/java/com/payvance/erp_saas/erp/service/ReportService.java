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
        return new VoucherLedgerDetailDTO(
                entry.getLedgerName(),
                entry.getAmount(), entry.getIsDebit(),
                entry.getIsPartyLedger());
    }
}

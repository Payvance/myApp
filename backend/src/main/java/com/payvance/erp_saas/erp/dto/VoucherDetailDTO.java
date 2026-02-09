package com.payvance.erp_saas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherDetailDTO {
    private Long id;
    private String guid;
    private String voucherNumber;
    private String voucherType;
    private LocalDate date;
    private String partyLedgerName;
    private BigDecimal totalAmount;
    private Boolean isInvoice;

    private String narration;
    private String deliveryNotes;
    private String paymentTerms;
    private String consigneeName;
    private String consigneeAddress;

    private List<VoucherInventoryDetailDTO> inventoryEntries;
    private List<VoucherLedgerDetailDTO> ledgerEntries;
}

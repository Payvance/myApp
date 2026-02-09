package com.payvance.erp_saas.erp.dto;

import lombok.Data;
import java.util.List;

@Data
public class VoucherDTO {
    private String guid;
    private String remoteId;
    private String voucherType;
    private String voucherNumber;
    private String voucherDate;
    private String partyLedgerGuid;
    private String partyLedgerName;
    private Long alterId;
    private Long masterId;
    private java.math.BigDecimal totalAmount;
    private Boolean isInvoice;
    private Boolean isDeleted;

    private String companyId;

    private List<VoucherInventoryDTO> inventoryEntries;
    private List<VoucherLedgerEntryDTO> ledgerEntries;

    private String narration;
    private String deliveryNotes;
    private String paymentTerms;
    private String consigneeName;
    private String consigneeAddress;
}

package com.payvance.erp_saas.erp.dto;

import lombok.Data;

@Data
public class VoucherLedgerEntryDTO {
    private String ledgerGuid;
    private String ledgerName;
    private java.math.BigDecimal amount;
    private Boolean isPartyLedger;
    private Boolean isDeemedPositive;
    private String methodType;

    // New Fields
    private String gstClass;
    private String gstNature;
    private java.math.BigDecimal cgstRate;
    private java.math.BigDecimal cgstAmount;
    private java.math.BigDecimal sgstRate;
    private java.math.BigDecimal sgstAmount;
    private java.math.BigDecimal igstRate;
    private java.math.BigDecimal igstAmount;

    // Classification & Cost Centers
    private String ledgerType;
    private String gstDutyHead;
    private String costCenterName;
    private String costCategoryName;
}

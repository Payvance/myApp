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
}

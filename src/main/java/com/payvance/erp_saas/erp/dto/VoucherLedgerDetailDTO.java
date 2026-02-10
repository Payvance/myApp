package com.payvance.erp_saas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherLedgerDetailDTO {
    private String ledgerName;
    private BigDecimal amount;
    private Boolean isDebit;
    private Boolean isPartyLedger;

    // Classification
    private String ledgerType;
    private String gstDutyHead;
    private String gstClass;
    private String costCenterName;
}

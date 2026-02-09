package com.payvance.erp_saas.erp.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class LedgerStatementDTO {
    private String ledgerName;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private BigDecimal currentTotalDebit;
    private BigDecimal currentTotalCredit;
    private List<LedgerStatementEntryDTO> entries;

    @Data
    @Builder
    public static class LedgerStatementEntryDTO {
        private String voucherId; // GUID
        private String voucherNumber;
        private String voucherType;
        private LocalDate date;
        private String particulars; // The 'other' ledger or party name
        private BigDecimal amount;
        private boolean isDebit;
        private BigDecimal runningBalance; // Calculated in service
        private String narration;
    }
}

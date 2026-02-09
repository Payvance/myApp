package com.payvance.erp_saas.core.dto;

import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class BankTransferApprovalRequest {

    private String status;          // APPROVED / REJECTED
    private String paymentMode;     // UPI / BANK_TRANSFER
    private String utrNumber;
    private String payerBank;
    private BigDecimal paidAmount;
    private LocalDate paidDate;
}

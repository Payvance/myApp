package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BankTransferRequestResponse {

    private LocalDateTime createdAt;
    private Long referralsCount;
    private BigDecimal amount;
    private String status;
    private String utrNumber;
    private String payerBank;
    private BigDecimal paidAmount;
}

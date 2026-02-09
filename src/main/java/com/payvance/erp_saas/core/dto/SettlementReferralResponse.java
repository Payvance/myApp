package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SettlementReferralResponse {

    private Long referralId;
    private Long referredTenantId;
    private BigDecimal rewardedAmount;
    private String status;
    private LocalDateTime createdAt;
}

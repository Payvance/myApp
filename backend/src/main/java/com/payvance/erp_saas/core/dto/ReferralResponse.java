package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Read-only DTO for referral listing API
 */
@Getter
@AllArgsConstructor
public class ReferralResponse {

    private Long referredTenantId;
    private String referredTenantName;
    private BigDecimal rewardedAmount;
    private String status;
    private LocalDateTime createdAt;
}

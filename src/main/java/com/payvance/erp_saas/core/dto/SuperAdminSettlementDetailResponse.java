package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SuperAdminSettlementDetailResponse {

    private Long bankTransferId;
    private Long tenantId;
    private long referralsCount;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private List<SettlementReferralResponse> referrals;
}

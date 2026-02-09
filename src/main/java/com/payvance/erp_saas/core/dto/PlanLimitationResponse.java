package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PlanLimitationResponse {
    private Long id;
    private Integer allowedUserCount;
    private Integer allowedCompanyCount;
    private Integer allowedUserCountTill;
    private Integer allowedCompanyCountTill;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

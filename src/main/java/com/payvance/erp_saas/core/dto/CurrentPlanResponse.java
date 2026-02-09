package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CurrentPlanResponse {

    private Long subscriptionId;
    private Long planId;
    private String planCode;
    private String planName;
    private String status;
    private LocalDateTime startAt;
    private LocalDateTime currentPeriodEnd;
}

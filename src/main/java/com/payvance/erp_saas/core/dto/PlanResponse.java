package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PlanResponse {
    private Long id;
    private String code;
    private String name;
    private String isActive;
    private PlanLimitationResponse planLimitation;
    private PlanPriceResponse planPrice;
}

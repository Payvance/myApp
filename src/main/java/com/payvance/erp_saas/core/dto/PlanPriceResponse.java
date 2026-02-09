package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PlanPriceResponse {
    private Long id;
    private String billingPeriod;
    private String currency;
    private BigDecimal amount;
    private Byte isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.payvance.erp_saas.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Plan Price.
 *
 * @author Aniket Desai
 */
@Data
public class PlanPriceDto {
    private Long id;

    @NotNull(message = "Billing period is required")
    @JsonProperty("billing_period")
    private String billingPeriod;

    private String currency = "INR";

    @DecimalMin(value = "0.0", inclusive = true, message = "Amount must be at least 0")
    private BigDecimal amount = BigDecimal.ZERO;

    @JsonProperty("is_active")
    private Byte isActive = 1;
}

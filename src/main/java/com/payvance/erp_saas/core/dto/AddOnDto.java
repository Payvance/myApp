package com.payvance.erp_saas.core.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.payvance.erp_saas.core.entity.Plan;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for Add-on details.
 *
 * @author Aniket Desai
 */
@Data
public class AddOnDto {

    private Long id;

    @NotEmpty(message = "Code is required")
    @Size(max = 60, message = "Code must be less than 60 characters")
    private String code;

    @NotEmpty(message = "Name is required")
    @Size(max = 150, message = "Name must be less than 150 characters")
    private String name;

    private String currency = "INR";

    @NotEmpty(message = "Unit is required")
    @Size(max = 30, message = "Unit must be less than 30 characters")
    private String unit;

    @DecimalMin(value = "0.0", inclusive = true, message = "Unit price must be at least 0")
    @JsonProperty("unit_price")
    private BigDecimal unitPrice = BigDecimal.ZERO;

    private String status = "active";
    
    @JsonProperty("plan_id")
    private Long planId;
}

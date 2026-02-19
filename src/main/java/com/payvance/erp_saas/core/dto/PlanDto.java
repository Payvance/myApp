package com.payvance.erp_saas.core.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for Plan Details.
 *
 * @author Aniket Desai
 */
@Data
public class PlanDto {
	private Boolean success;
	private String message;
    private Long id;

    @NotEmpty(message = "Code is required")
    @Size(max = 60, message = "Code must be less than 60 characters")
    private String code;

    @NotEmpty(message = "Name is required")
    @Size(max = 120, message = "Name must be less than 120 characters")
    private String name;

    @JsonProperty("is_active")
    private String isActive = "1";

    @JsonProperty("is_separate_db")
    private String isSeparateDb = "0";
    
    private List<Long> tenantIds;


    @Valid
    @NotNull(message = "Plan limitation details are required")
    @JsonProperty("plan_limitation")
    private PlanLimitationDto planLimitation;

    @Valid
    @NotNull(message = "Plan price details are required")
    @JsonProperty("plan_price")
    private PlanPriceDto planPrice;
}

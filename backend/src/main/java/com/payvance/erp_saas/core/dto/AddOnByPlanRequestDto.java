package com.payvance.erp_saas.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddOnByPlanRequestDto {
	 
	@NotNull(message = "Plan ID is required")
	    private Long planId;

}

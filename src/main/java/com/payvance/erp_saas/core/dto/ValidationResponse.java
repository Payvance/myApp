package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationResponse {
	
	private String status;
    private String message;
}

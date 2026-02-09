package com.payvance.erp_saas.core.dto;

import lombok.Data;

@Data
public class LicenseCheckDto {
	
	// INPUT
    private String email;
    private String phone;
    private Long userId; // vendor's userId

    // OUTPUT
    private Boolean eligible;
    private Long tenantId;
    private String tenantName;
    private String tenantEmail;   
    private String tenantPhone;   
    private Long vendorId;
    private String message;
}

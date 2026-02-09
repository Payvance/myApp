package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TenantUserResponseDto {
	 private Long userId;
	    private String name;
	    private String email;
	    private String phone;
	    private Boolean tenantUserActive; // From TenantUserRole
	    private Long tenantId;   
	    private String tenantStatus;
	

}

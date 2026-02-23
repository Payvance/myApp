package com.payvance.erp_saas.core.dto;


import lombok.Data;

@Data

public class TenantUserResponseDto {
	 private Long userId;
	    private String name;
	    private String email;
	    private String phone;
	    private Boolean tenantUserActive; // From TenantUserRole
	    private Long tenantId;   
	    private String tenantStatus;
	    private Long roleId;
		public TenantUserResponseDto(Long userId, String name, String email, String phone, Boolean tenantUserActive,
				Long tenantId, String tenantStatus, Long roleId) {
			super();
			this.userId = userId;
			this.name = name;
			this.email = email;
			this.phone = phone;
			this.tenantUserActive = tenantUserActive;
			this.tenantId = tenantId;
			this.tenantStatus = tenantStatus;
			this.roleId = roleId;
		}
	    
	

}

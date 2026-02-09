package com.payvance.erp_saas.core.dto;

import lombok.Data;

@Data
public class TenantUserStatusRequest {
	 private Long userId;      // user to update
	    private boolean active; 
	    private String name;
	    
	    

}

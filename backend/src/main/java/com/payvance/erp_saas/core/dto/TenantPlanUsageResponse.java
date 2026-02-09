package com.payvance.erp_saas.core.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantPlanUsageResponse {

	  private Long tenantId;
	  private Long subscriptionId;

	    private Long planId;
	    private String planCode;
	    private String planName;

	    private LocalDateTime planExpiry;

	    private Integer activeUsers;
	    private Integer activeCompanies;
	    
	    private List<AddonResponse> addons;
}

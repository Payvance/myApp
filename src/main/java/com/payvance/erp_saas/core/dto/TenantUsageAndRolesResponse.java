package com.payvance.erp_saas.core.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantUsageAndRolesResponse {
	
	private Long tenantId;

    private Integer activeUsers;
    private Integer activeCompanies;

    private List<TenantRoleResponse> roles;

}

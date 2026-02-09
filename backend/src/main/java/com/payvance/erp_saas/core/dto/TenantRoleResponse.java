package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantRoleResponse {
	private Long userId;
    private Long roleId;
    private Boolean status;

}

package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantUserWithRoleDto {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private Boolean tenantUserActive;
    private Long tenantId;
    private String tenantStatus;
    private Long roleId;
    private String roleName;
}

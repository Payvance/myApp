package com.payvance.erp_saas.core.dto;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantWithAdminAndUsersResponse {
    
    private Long tenantadmin;
    private String name;
    private String email;
    private String phone;
    private Boolean adminActive;
    private Long adminRoleId;
    private String adminRoleName;
    private Long tenantId;
    private String tenantStatus;
    
    private Page<TenantUserWithRoleDto> tenantUsers;
}

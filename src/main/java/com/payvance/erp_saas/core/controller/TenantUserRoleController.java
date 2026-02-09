package com.payvance.erp_saas.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.dto.TenantUsageAndRolesResponse;
import com.payvance.erp_saas.core.dto.TenantUserStatusRequest;
import com.payvance.erp_saas.core.service.TenantUserRoleService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/tenant-users")
@RequiredArgsConstructor
public class TenantUserRoleController {
	
	private final TenantUserRoleService tenantUserRoleService;

    @PostMapping("/status")
    public ResponseEntity<String> updateTenantUserStatus(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @RequestBody TenantUserStatusRequest request) {

        tenantUserRoleService.updateTenantUserStatus(
                tenantId,
                request.getUserId(),
                request.isActive(),
                request.getName()
        );

        return ResponseEntity.ok("Tenant user status updated successfully");
    }
    
    /*
     * Get tenant usage and roles
     */
    @GetMapping("/usage-roles")
    public ResponseEntity<TenantUsageAndRolesResponse> getTenantUsageAndRoles(
            @RequestHeader("X-Tenant-Id") Long tenantId
    ) {
        return ResponseEntity.ok(
        		tenantUserRoleService.getTenantUsageAndRoles(tenantId)
        );
    }

}

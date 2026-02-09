package com.payvance.erp_saas.core.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.dto.TenantPlanResponse;
import com.payvance.erp_saas.core.dto.TenantPlanUsageResponse;
import com.payvance.erp_saas.core.service.TenantPlanService;
import com.payvance.erp_saas.core.service.TenantPlanUsageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantPlanController {

    private final TenantPlanService service;
    
    private final TenantPlanUsageService tenantPlanUsageService;

    @GetMapping("/plans")
    public ResponseEntity<TenantPlanResponse> getTenantPlans(
            @RequestHeader("tenant_id") Long tenantId
    ) {
        return ResponseEntity.ok(
                service.getTenantPlans(tenantId)
        );
    }
    
    
    /*
     * Get paginated list of tenant plan usage
     */
    @GetMapping("/plan-usage")
    public ResponseEntity<Page<TenantPlanUsageResponse>> getTenantPlanUsage(
    		@RequestHeader("X-Tenant-Id") Long tenantId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                tenantPlanUsageService.getTenantPlanUsage(tenantId,pageable)
        );
    }
}

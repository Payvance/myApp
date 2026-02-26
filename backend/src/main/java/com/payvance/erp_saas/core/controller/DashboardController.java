package com.payvance.erp_saas.core.controller;

import com.payvance.erp_saas.core.service.SuperAdminDashboardService;
import com.payvance.erp_saas.core.service.VendorDashboardService;
import com.payvance.erp_saas.core.service.TenantDashboardService;
import com.payvance.erp_saas.core.service.CADashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final SuperAdminDashboardService superAdminDashboardService;
    private final VendorDashboardService vendorDashboardService;
    private final TenantDashboardService tenantDashboardService;
    private final CADashboardService caDashboardService;

    @PostMapping("/{roleId}")
    public ResponseEntity<Map<String, Object>> getDashboardData(
            @PathVariable Long roleId, 
            @RequestBody Map<String, Object> payload) {
        Object userIdObj = payload.get("userId");
        Long userId;
        if (userIdObj instanceof String) {
            userId = Long.valueOf((String) userIdObj);
        } else if (userIdObj instanceof Number) {
            userId = ((Number) userIdObj).longValue();
        } else {
            throw new IllegalArgumentException("Invalid userId type");
        }
        
        Integer startYear = (Integer) payload.get("startYear");
        Integer endYear = (Integer) payload.get("endYear");
        
        Map<String, Object> dashboardData = getDashboardDataByRole(roleId, userId, startYear, endYear);
        return ResponseEntity.ok(dashboardData);
    }

    private Map<String, Object> getDashboardDataByRole(Long roleId, Long userId, Integer startYear, Integer endYear) {
        int roleIdInt = roleId.intValue();
        switch (roleIdInt) {
            case 1: // Superadmin
                return superAdminDashboardService.getDashboardData();
            case 4: // Vendor
                return vendorDashboardService.getDashboardData(userId, startYear, endYear);
            case 2: // Tenant
                return tenantDashboardService.getDashboardData(userId, startYear, endYear);
            case 5: // CA
                return caDashboardService.getDashboardData(userId);
            default: // Default response for unknown roles
                Map<String, Object> emptyData = new HashMap<>();
                emptyData.put("cards", Arrays.asList());
                emptyData.put("pieCharts", Arrays.asList());
                emptyData.put("barCharts", Arrays.asList());
                emptyData.put("dataViews", Arrays.asList());
                return emptyData;
        }
    }
}

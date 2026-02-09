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
    public ResponseEntity<Map<String, Object>> getDashboardData(@PathVariable Long roleId) {
        Map<String, Object> dashboardData = getDashboardDataByRole(roleId);
        return ResponseEntity.ok(dashboardData);
    }

    private Map<String, Object> getDashboardDataByRole(Long roleId) {
        int roleIdInt = roleId.intValue();
        switch (roleIdInt) {
            case 1: // Superadmin
                return superAdminDashboardService.getDashboardData();
            case 4: // Vendor
                return vendorDashboardService.getDashboardData();
            case 2: // Tenant
                return tenantDashboardService.getDashboardData();
            case 5: // CA
                return caDashboardService.getDashboardData();
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

package com.payvance.erp_saas.core.controller;

import com.payvance.erp_saas.core.dto.CATenantListDTO;
import com.payvance.erp_saas.core.service.CATenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for CA Tenant operations
 * 
 * @author system
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/ca-tenants")
@RequiredArgsConstructor
public class CATenantController {
    
    private final CATenantService caTenantService;
    
    /**
     * Get paginated list of tenants for a CA
     */
    @GetMapping("/pagination")
    public ResponseEntity<Page<CATenantListDTO>> getTenantsForCa(Pageable pageable, @RequestParam Long caUserId) {
        return ResponseEntity.ok(caTenantService.getTenantsForCa(caUserId, pageable));
    }
    
    /**
     * Update tenant request status
     */
    @PostMapping("/update-status")
    public ResponseEntity<Map<String, Object>> updateTenantStatus(@RequestBody Map<String, Object> payload) {
        Long caUserId = Long.valueOf(payload.get("caUserId").toString());
        Long tenantId = Long.valueOf(payload.get("tenantId").toString());
        Integer isView = Integer.valueOf(payload.get("isView").toString());
        
        Map<String, Object> response = caTenantService.updateTenantStatus(caUserId, tenantId, isView);
        return ResponseEntity.ok(response);
    }
}

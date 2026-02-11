package com.payvance.erp_saas.core.controller;

import com.payvance.erp_saas.core.service.TenantManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for Tenant Management operations
 * 
 * @author system
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/tenantca-management")
@RequiredArgsConstructor
public class TenantManagementController {
    
    private final TenantManagementService tenantManagementService;
    
    /**
     * Process tenant management request
     * Expected payload: { "userId": 123, "caNo": "CA123", "referenceCode": "REF456" }
     */
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processTenantRequest(@RequestBody Map<String, Object> payload) {
        // Check if all required fields are present
        if (!payload.containsKey("userId") || !payload.containsKey("caNo") || !payload.containsKey("referenceCode")) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "Missing required fields: userId, caNo, referenceCode"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Check if any field is null or empty
        if (payload.get("userId") == null || payload.get("caNo") == null || payload.get("referenceCode") == null ||
            payload.get("userId").toString().trim().isEmpty() || 
            payload.get("caNo").toString().trim().isEmpty() || 
            payload.get("referenceCode").toString().trim().isEmpty()) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "All fields are required and cannot be empty: userId, caNo, referenceCode"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        Long userId = Long.valueOf(payload.get("userId").toString());
        String caNo = payload.get("caNo").toString();
        String referenceCode = payload.get("referenceCode").toString();
        
        Map<String, Object> response = tenantManagementService.processTenantRequest(userId, caNo, referenceCode);
        
        if ((Boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}

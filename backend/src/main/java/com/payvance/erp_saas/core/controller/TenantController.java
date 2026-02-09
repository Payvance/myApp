/**
 * Copyright: Â© 2024 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of Payvance Innovation Pvt. Ltd.
 * Payvance Innovation Pvt. Ltd. retains all title to and intellectual property rights in these materials.
 *
 **/

/**
 *
 * @author           version     date        change description
 * Anjor         	 1.0.0       28-Dec-2025    class created
 *
 **/
package com.payvance.erp_saas.core.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.config.TrialConfig;
import com.payvance.erp_saas.core.dto.TenantSignupRequest;
import com.payvance.erp_saas.core.dto.TenantTrialRequest;
import com.payvance.erp_saas.core.entity.Tenant;
import com.payvance.erp_saas.core.enums.RoleEnum;
import com.payvance.erp_saas.core.service.PartnerSignupService;
import com.payvance.erp_saas.core.service.TenantService;
import com.payvance.erp_saas.core.service.TenantSignupService;
import com.payvance.erp_saas.exceptions.UserNotAllowedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantSignupService signupService;
    private final PartnerSignupService partnerSignupService;
    private final TenantService tenantService;

    // Signup endpoint for tenants and partners
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody TenantSignupRequest req) {
    	
    	 // CASE 1: User joining existing tenant
        if (req.getExistingTenantId() != null) {
            signupService.registerUserWithExistingTenant(req);
            return ResponseEntity.ok(
                    "User registered and mapped to tenant. Verify email.");
        }
    	
    	
        // If role is not provided in payload, create only the user row (no tenant/partner creation)
        if (req.getRole() == null) {
            signupService.registerUserOnly(req);
            return ResponseEntity.ok("User registered successfully. Verify email.");
        }

        RoleEnum role = RoleEnum.fromId(req.getRole());

        switch (role) {

            case VENDOR:
            case CA:
                partnerSignupService.signupPartner(req);
                break;

            case TENANT_ADMIN:
                signupService.registerTenant(req);
                break;

            default:
                throw new UserNotAllowedException("Signup not allowed for role: " + role);
        }

        return ResponseEntity.ok("Registered successfully. Verify email.");
    }

    @PostMapping("/trial")
    public ResponseEntity<?> startTrial(@RequestBody TenantTrialRequest req) {
        signupService.registerTenantForTrial(req);
        return ResponseEntity.ok("Trial started successfully.");
    }

    @PostMapping("/start-trial")
    public ResponseEntity<?> startTrialByUser(@RequestBody com.payvance.erp_saas.core.dto.StartTrialByUserRequest req) {
        Long userId = req.getUserId();

        if (userId == null) {
            return ResponseEntity.badRequest().body("user_id is required");
        }

        var period = tenantService.startTrialByUser(userId);
        return ResponseEntity.ok(Map.of(
                "trial_start_at", period.getTrialStartAt(),
                "trial_end_at", period.getTrialEndAt()
        ));
    }
    
    /*
     * Get tenant details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getTenantById(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.getTenantById(id));
    }
    
    /*
     * Get trial configuration
     */
    @GetMapping("/trial-config")
    public ResponseEntity<TrialConfig> getTrialConfig() {
        return ResponseEntity.ok(tenantService.getTrialConfig());
    }

    
    /*
     * 
     * Get tenants associated with a user by user ID
     */
    @GetMapping("/by-user/tenantdetails")
    public ResponseEntity<List<Tenant>> getTenantsByUser(
            @RequestHeader("X-User-Id") Long userId) {

        return ResponseEntity.ok(tenantService.getTenantsByUserId(userId));
    }

}

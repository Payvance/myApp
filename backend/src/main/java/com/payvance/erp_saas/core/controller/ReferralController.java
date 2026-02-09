package com.payvance.erp_saas.core.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.dto.ReferralResponse;
import com.payvance.erp_saas.core.service.ReferralProgramService;
import com.payvance.erp_saas.core.service.ReferralService;

import lombok.RequiredArgsConstructor;

/**
 * Controller for referral-related APIs (CA side)
 */
@RestController
@RequestMapping("/api/ca/referrals/redemptions")
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService referralService;
    private final ReferralProgramService referralProgramService;

    /**
     * Fetch referrals for logged-in CA
     * userId is taken from request header and mapped with referrer_id
     */
    @GetMapping
    public ResponseEntity<Page<ReferralResponse>> getReferrals(
            @RequestHeader("userId") Long userId,   // Logged-in user (CA)
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long referredTenantId,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                referralService.getReferrals(
                        userId,              // referrerId
                        status,
                        referredTenantId,
                        fromDate,
                        toDate,
                        pageable
                )
        );
    }
    
    /*
     * Get referral program details for CA
     */
    @GetMapping("/referral/details")
    public ResponseEntity<?> getReferralDetails(
            @RequestHeader("X-Tenant-Id") Long tenantId) {

        return ResponseEntity.ok(
        		referralProgramService.getReferralDetails(tenantId)
        );
    }
}

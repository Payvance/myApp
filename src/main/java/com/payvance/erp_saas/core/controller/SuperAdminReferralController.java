package com.payvance.erp_saas.core.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;  
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;  
import com.payvance.erp_saas.core.service.ReferralService;
import com.payvance.erp_saas.core.dto.ReferralResponse;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/super-admin/referrals")
@RequiredArgsConstructor
public class SuperAdminReferralController {

    private final ReferralService referralService;

    /**
     * Fetch all referrals for Super Admin
     */
    @GetMapping
    public ResponseEntity<Page<ReferralResponse>> getAllReferrals(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long referredTenantId,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                referralService.getAllReferrals(
                        status,
                        referredTenantId,
                        fromDate,
                        toDate,
                        pageable
                )
        );
    }
}


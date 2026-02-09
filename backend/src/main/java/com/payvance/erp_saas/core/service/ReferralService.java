package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.dto.ReferralResponse;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for referral APIs
 */
public interface ReferralService {

    Page<ReferralResponse> getReferrals(
            Long referrerId,          // userId from header
            String status,
            Long referredTenantId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    );


    /**
     * * Fetch all referrals for Super Admin
     */ 
    Page<ReferralResponse> getAllReferrals(
        String status,
        Long referredTenantId,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        Pageable pageable
);

}

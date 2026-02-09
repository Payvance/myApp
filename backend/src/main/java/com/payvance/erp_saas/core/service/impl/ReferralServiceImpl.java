package com.payvance.erp_saas.core.service.impl;

import com.payvance.erp_saas.core.dto.ReferralResponse;
import com.payvance.erp_saas.core.repository.ReferralRepository;
import com.payvance.erp_saas.core.service.ReferralService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementation of referral service
 */
@Service
@RequiredArgsConstructor
public class ReferralServiceImpl implements ReferralService {

    private final ReferralRepository referralRepository;

    @Override
    public Page<ReferralResponse> getReferrals(
            Long referrerId,
            String status,
            Long referredTenantId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    ) {
        return referralRepository.findReferralData(
                referrerId,
                status,
                referredTenantId,
                fromDate,
                toDate,
                pageable
        );
    }

    // Fetch all referrals for Super Admin
    @Override
    public Page<ReferralResponse> getAllReferrals(
            String status,
            Long referredTenantId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    ) {
        return referralRepository.findAllReferralData(
                status,
                referredTenantId,
                fromDate,
                toDate,
                pageable
        );
    }

}

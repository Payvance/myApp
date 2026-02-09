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
package com.payvance.erp_saas.core.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.payvance.erp_saas.core.repository.PersonalAccessTokenRepository;

import jakarta.transaction.Transactional;

import java.time.Instant;

@Service
public class TokenCleanupService {

    private final PersonalAccessTokenRepository patRepo;

    public TokenCleanupService(PersonalAccessTokenRepository patRepo) {
        this.patRepo = patRepo;
    }

    // Run every hour
    @Transactional
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void deleteExpiredTokens() {
        Instant now = Instant.now();
        patRepo.deleteAllByExpiresAtBefore(now);
        System.out.println("Expired tokens cleaned up at " + now);
    }
}

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
package com.payvance.erp_saas.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.payvance.erp_saas.core.entity.PersonalAccessToken;
import java.util.Optional;

public interface PersonalAccessTokenRepository extends JpaRepository<PersonalAccessToken, Long> {
    boolean existsByTokenId(String tokenId);

    boolean existsByUserId(Long userId);

    Optional<PersonalAccessToken> findByTokenId(String tokenId);

    @Modifying
    @Query("DELETE FROM PersonalAccessToken p WHERE p.tokenId = :tokenId")
    void deleteByTokenId(String tokenId);

    void deleteAllByUserId(Long userId);

    void deleteAllByExpiresAtBefore(java.time.Instant now);
}

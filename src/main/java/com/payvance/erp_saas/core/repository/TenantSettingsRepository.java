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
 * Anjor         	 1.0.0       29-Dec-2025    class created
 *
 **/
package com.payvance.erp_saas.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.payvance.erp_saas.core.entity.TenantSetting;

public interface TenantSettingsRepository extends JpaRepository<TenantSetting, Long> {

    Optional<TenantSetting> findByTenantId(Long tenantId);

	
	@Modifying
    @Query("""
        UPDATE TenantSetting ts
        SET ts.adsUnlockedEnabled = :enabled
        WHERE ts.tenantId = :tenantId
    """)
    int updateAdsUnlocked(@Param("tenantId") Long tenantId, @Param("enabled") boolean enabled);
	
	
}

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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.payvance.erp_saas.core.entity.TenantUsage;

import java.util.Optional;

public interface TenantUsageRepository extends JpaRepository<TenantUsage, Long> {

    Optional<TenantUsage> findByTenantId(Long tenantId);
    
    /*
     * Get companies count directly from companies_count column
     */
    @Query("SELECT tu.companiesCount FROM TenantUsage tu WHERE tu.tenantId = :tenantId")
    Integer findCompaniesCountByTenantId(@Param("tenantId") Long tenantId);
    boolean existsByTenantId(Long tenantId);
}

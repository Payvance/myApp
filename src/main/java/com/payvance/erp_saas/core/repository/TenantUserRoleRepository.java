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
 * Anjor         	 1.0.0       26-Dec-2025    class created
 *
 **/
package com.payvance.erp_saas.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.core.entity.TenantUserRole;

@Repository
public interface TenantUserRoleRepository
        extends JpaRepository<TenantUserRole, Long> {

    Optional<TenantUserRole> findFirstByUserIdAndIsActiveTrueOrderByRoleIdAsc(Long userId);

    Optional<TenantUserRole> findByUserIdAndTenantIdAndRoleIdAndIsActiveTrue(Long userId, Long tenantId, Long roleId);

    Optional<TenantUserRole> findByUserIdAndRoleIdAndIsActiveTrue(Long userId, Long roleId);

    Optional<TenantUserRole> findByUserIdAndTenantId(Long userId, Long tenantId);
    
    /*
     * Find all TenantUserRole entries by tenantId and roleId
     */
    List<TenantUserRole> findByTenantIdAndRoleId(
            Long tenantId,
            Long roleId
    );

	Optional<TenantUserRole> findFirstByTenantIdAndIsActiveTrue(Long tenantId);
	
	
	/*
	 * Fetch distinct tenant IDs associated with a given user ID
	 */
	@Query("""
		    SELECT DISTINCT tur.tenantId
		    FROM TenantUserRole tur
		    WHERE tur.userId = :userId
		""")
		List<Long> findTenantIdsByUserId(@Param("userId") Long userId);


	
}

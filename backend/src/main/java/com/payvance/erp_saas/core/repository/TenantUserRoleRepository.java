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
import java.util.Map;
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

	/*
	 * Count users by tenant ID
	 */
	@Query("SELECT COUNT(DISTINCT tur.userId) FROM TenantUserRole tur WHERE tur.tenantId = :tenantId")
	Long countUsersByTenantId(@Param("tenantId") Long tenantId);
	
	/*
	 * Count active and inactive users by tenant ID (single query)
	 */
	@Query("SELECT new map(COUNT(DISTINCT CASE WHEN tur.isActive = true THEN tur.userId END) as activeUsers, COUNT(DISTINCT CASE WHEN tur.isActive = false THEN tur.userId END) as inactiveUsers) FROM TenantUserRole tur WHERE tur.tenantId = :tenantId")
	Map<String, Long> countActiveInactiveUsersByTenantId(@Param("tenantId") Long tenantId);
    
    /*
     * Find 5 most recent users for tenant
     */
    @Query("""
        SELECT new map(
            tur.userId as userId,
            u.name as userName,
            u.email as userEmail,
            tur.createdAt as createdAt,
            tur.isActive as isActive
        )
        FROM TenantUserRole tur
        JOIN User u ON tur.userId = u.id
        WHERE tur.tenantId = :tenantId
        ORDER BY tur.createdAt DESC
        """)
    List<Map<String, Object>> findRecentUsersByTenantId(@Param("tenantId") Long tenantId);
}

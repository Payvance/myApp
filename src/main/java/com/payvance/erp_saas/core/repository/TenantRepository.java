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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.payvance.erp_saas.core.entity.Tenant;

import jakarta.persistence.LockModeType;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("""
                UPDATE Tenant t
                SET t.status = 'trial',
                    t.trialStartAt = :trialStartAt,
                    t.trialEndAt = :trialEndAt
                WHERE t.id = :tenantId
            """)
    int updateTenantStatusToTrial(
            @Param("tenantId") Long tenantId,
            @Param("trialStartAt") LocalDateTime trialStartAt,
            @Param("trialEndAt") LocalDateTime trialEndAt);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Tenant t WHERE t.id = :id")
    Optional<Tenant> findByIdForUpdate(@Param("id") Long id);
    
    /*
     * 
     * Check eligibility of tenant based on email and phone number having active role with roleId = 2
     */
    @Query("""
    	    SELECT t.id, t.name, t.email, t.phone
    	    FROM Tenant t
    	    JOIN TenantUserRole tur ON tur.tenantId = t.id
    	    WHERE (t.email = :email OR t.phone = :phone)
    	      AND tur.roleId = 2
    	      AND tur.isActive = true
    	""")
    	List<Object[]> checkEligibility(
    	        @Param("email") String email,
    	        @Param("phone") String phone
    	);

    	
    	/*
    	 * Fetch tenant name by tenant id
    	 */
    	@Query("SELECT t.name FROM Tenant t WHERE t.id = :tenantId")
    	Optional<String> findTenantNameById(@Param("tenantId") Long tenantId);
    	
    	@Modifying
    	@Query("""
    	    UPDATE Tenant t
    	    SET t.status = :status
    	    WHERE t.id = :tenantId
    	""")
    	void updateTenantStatus(
    	        @Param("tenantId") Long tenantId,
    	        @Param("status") String status
    	);

    /**
     * Get total count of tenants
     */
    @Query("SELECT COUNT(t) FROM Tenant t")
    Long getTotalTenants();

}

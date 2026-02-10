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

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.payvance.erp_saas.core.dto.TenantUserResponseDto;
import com.payvance.erp_saas.core.dto.UserFullDetailsDto;
import com.payvance.erp_saas.core.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);
    
    Optional<User> findByDesktopDeviceId(String desktopDeviceId);
    
    
    /*
     * Fetch paginated list of users with basic details
     */
    @Query("""
    	    SELECT new map(
    	        u.id as id,
    	        u.name as name,
    	        u.email as email,
    	        u.phone as phone,
    	        u.isActive as active,

    	       CASE
            WHEN u.isSuperadmin = true THEN 1
            WHEN tur.roleId = 2 THEN 2
            WHEN tur.roleId IS NOT NULL THEN tur.roleId
            WHEN v.id IS NOT NULL THEN 3
            WHEN c.id IS NOT NULL THEN 4
        END as roleId,

        CASE
            WHEN u.isSuperadmin = true THEN 'SUPERADMIN'
            WHEN tur.roleId = 2 THEN 'TENANT_ADMIN'
            WHEN tur.roleId IS NOT NULL THEN 'TENANT'
            WHEN v.id IS NOT NULL THEN 'VENDOR'
            WHEN c.id IS NOT NULL THEN 'CA'
        END as roleName
    	    )
    	    FROM User u
    	    LEFT JOIN TenantUserRole tur ON tur.userId = u.id AND tur.isActive = true
    	    LEFT JOIN Vendor v ON v.userId = u.id
    	    LEFT JOIN Ca c ON c.userId = u.id
    	""")
    	Page<Map<String, Object>> findAllUsersBasic(Pageable pageable);

    
    
    /*
     * Fetch paginated list of inactive users with basic details
     */
    @Query("""
    	    SELECT new map(
    	        u.id as id,
    	        u.name as name,
    	        u.email as email,
    	        u.phone as phone,

    	        CASE
    	            WHEN EXISTS (
    	                SELECT 1 FROM Vendor v
    	                WHERE v.userId = u.id
    	                  AND v.status = 'PENDING_APPROVAL'
    	            ) THEN 3
    	            WHEN EXISTS (
    	                SELECT 1 FROM Ca c
    	                WHERE c.userId = u.id
    	                  AND c.status = 'PENDING_APPROVAL'
    	            ) THEN 4
    	        END as roleId,

    	        CASE
    	            WHEN EXISTS (
    	                SELECT 1 FROM Vendor v
    	                WHERE v.userId = u.id
    	                  AND v.status = 'PENDING_APPROVAL'
    	            ) THEN 'VENDOR'
    	            WHEN EXISTS (
    	                SELECT 1 FROM Ca c
    	                WHERE c.userId = u.id
    	                  AND c.status = 'PENDING_APPROVAL'
    	            ) THEN 'CA'
    	        END as roleName
    	    )
    	    FROM User u
    	    WHERE
    	        EXISTS (
    	            SELECT 1 FROM Vendor v
    	            WHERE v.userId = u.id
    	              AND v.status = 'PENDING_APPROVAL'
    	        )
    	        OR
    	        EXISTS (
    	            SELECT 1 FROM Ca c
    	            WHERE c.userId = u.id
    	              AND c.status = 'PENDING_APPROVAL'
    	        )
    	""")
    	Page<Map<String, Object>> findPendingVendorAndCaUsers(Pageable pageable);
        
    
    /*
     * Fetch full details of a user by user ID
     */
    @Query("""
    		SELECT new com.payvance.erp_saas.core.dto.UserFullDetailsDto(
    		    u.id, u.name, u.email, u.phone, u.isActive,

    		    CASE
            WHEN u.isSuperadmin = true THEN 'SUPERADMIN'
            WHEN c.userId IS NOT NULL THEN 'CA'
            WHEN v.userId IS NOT NULL THEN 'VENDOR'
            WHEN tur.roleId = 2 THEN 'TENANT_ADMIN'
            WHEN tur.roleId = 3 THEN 'TENANT_USER'
        END,

    		    c.id, c.caRegNo, c.enrollmentYear, c.icaiMemberStatus,
    		    c.practiceType, c.firmName, c.icaiMemberNo, c.status, c.caType,
                c.gstNo, c.cinNo, c.panNo, c.tanNo,
                
    		    v.id, v.name, v.vendorType,v.vendorDiscountId, v.experienceYears,
                v.gstNo, v.cinNo, v.panNo, v.tanNo,
                v.aadharNo, v.status,
    		    ua.id, ua.houseBuildingNo, ua.houseBuildingName,
    		    ua.roadAreaPlace, ua.landmark, ua.village, ua.taluka,
    		    ua.city, ua.district, ua.state, ua.pincode,
    		    ua.postOffice, ua.country,

    		    bd.id, bd.bankName, bd.branchName,
    		    bd.accountNumber, bd.ifscCode
    		)
    		FROM User u
    		LEFT JOIN Ca c ON c.userId = u.id
    		LEFT JOIN Vendor v ON v.userId = u.id
    		LEFT JOIN TenantUserRole tur 
           ON tur.userId = u.id 
          AND tur.isActive = true
    		LEFT JOIN UserAddress ua ON ua.userId = u.id
    		LEFT JOIN BankDetails bd ON bd.userId = u.id
    		WHERE u.id = :userId
    		""")
    		Optional<UserFullDetailsDto> findFullDetailsByUserId(@Param("userId") Long userId);

    
    @Query("""
    	    SELECT new com.payvance.erp_saas.core.dto.TenantUserResponseDto(
    	        u.id,
    	        u.name,
    	        u.email,
    	        u.phone,
    	        tur.isActive,
    	        tur.tenantId,
    	        t.status
    	    )
    	    FROM User u
    	    JOIN TenantUserRole tur ON u.id = tur.userId
    	    JOIN Tenant t ON t.id = tur.tenantId
    	    WHERE tur.roleId = 3
    	      AND tur.tenantId = :tenantId
    	""")
        Page<TenantUserResponseDto> findTenantUsersByTenantId(Long tenantId, Pageable pageable);
    
    @Query("""
    	    SELECT new com.payvance.erp_saas.core.dto.TenantUserResponseDto(
    	        u.id,
    	        u.name,
    	        u.email,
    	        u.phone,
    	        tur.isActive,
    	        tur.tenantId,
    	        t.status
    	    )
    	    FROM User u
    	    JOIN TenantUserRole tur ON u.id = tur.userId
    	    JOIN Tenant t ON t.id = tur.tenantId
    	    WHERE tur.roleId = 3
    	      AND tur.tenantId = :tenantId
    	      AND u.id = :userId
    	""")
    	Optional<TenantUserResponseDto> findTenantUserByTenantIdAndUserId(
    	        @Param("tenantId") Long tenantId,
    	        @Param("userId") Long userId
    	);

    /*
     * Count guest users (users not in tenant/vendor/ca roles and not superadmin)
     */
    @Query("""
            SELECT COUNT(u.id) 
            FROM User u 
            LEFT JOIN TenantUserRole tur ON u.id = tur.userId
            LEFT JOIN Vendor v ON u.id = v.userId
            LEFT JOIN Ca c ON u.id = c.userId
            WHERE tur.userId IS NULL 
              AND v.userId IS NULL 
              AND c.userId IS NULL 
              AND u.isSuperadmin = false
            """)
    Long countGuestUsers();

    /*
     * Find tenantId or vendorId based on role and userId
     */
    @Query("""
            SELECT 
                CASE 
                    WHEN :roleId = 2 THEN t.id
                    WHEN :roleId = 4 THEN v.id
                    ELSE NULL
                END
            FROM User u
            LEFT JOIN Tenant t ON t.email = u.email
            LEFT JOIN Vendor v ON v.email = u.email
            WHERE u.id = :userId
            """)
    Optional<Long> findEntityIdByUserAndRole(
            @Param("userId") Long userId,
            @Param("roleId") Integer roleId
    );

}

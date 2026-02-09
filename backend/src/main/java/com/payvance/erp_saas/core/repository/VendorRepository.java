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

import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.payvance.erp_saas.core.entity.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByUserId(Long userId);
    Optional<Vendor> findByIdAndUserId(Long id, Long userId);
    
    @Query("""
    	    SELECT new map(vd.type as type, vd.value as value)
    	    FROM Vendor v
    	    JOIN VendorDiscount vd ON v.vendorDiscountId = vd.id
    	    WHERE v.userId = :userId
    	""")
    	Map<String, Object> findDiscountTypeAndValueByUserId(@Param("userId") Long userId);
    
    Optional<Vendor> findByUserIdAndStatus(Long userId, String status);
    
    /**
     * Count vendors by status
     */
    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.status = :status")
    Long countByStatus(@Param("status") String status);
    
}

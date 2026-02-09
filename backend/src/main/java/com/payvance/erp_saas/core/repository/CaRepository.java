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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.payvance.erp_saas.core.entity.Ca;

import java.util.Optional;

public interface CaRepository extends JpaRepository<Ca, Long> {
    Optional<Ca> findByUserId(Long userId);
    Optional<Ca> findByUserIdAndStatus(Long userId, String status);
    
    /**
     * Count CAs by status
     */
    @Query("SELECT COUNT(c) FROM Ca c WHERE c.status = :status")
    Long countByStatus(@Param("status") String status);

}

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
 * Aniket Desai  	 1.0.0       06-Jan-2026    class created
 *
 **/
package com.payvance.erp_saas.core.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.core.dto.ActivationKeyListDTO;
import com.payvance.erp_saas.core.entity.ActivationKey;

@Repository
public interface ActivationKeyRepository
        extends JpaRepository<ActivationKey, Long>, JpaSpecificationExecutor<ActivationKey> {
    List<ActivationKey> findByVendorBatchId(Long vendorBatchId);
    
    
    /*
     * * Method to fetch paginated list of Activation Keys with selected fields
     */
    @Query("""
            SELECT new com.payvance.erp_saas.core.dto.ActivationKeyListDTO(
                a.id,
                a.plainCodeLast4,
                a.issuedToEmail,
                a.issuedToPhone,
                a.status,
                a.expiresAt
            )
            FROM ActivationKey a
            """)
        Page<ActivationKeyListDTO> findAllKeys(Pageable pageable);
    
    
    // Fetch all active licenses that should block a new one
    @Query("SELECT a FROM ActivationKey a WHERE a.redeemedTenantId = :tenantId " +
           "AND (a.status = 'ISSUED' OR a.status = 'REDEEMED') " +
           "AND a.expiresAt > :now")
    List<ActivationKey> findActiveBlockingKeys(
            @Param("tenantId") Long tenantId,
            @Param("now") LocalDateTime now
    );
}

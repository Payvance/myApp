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

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.core.dto.VendorActivationBatchResponseDTO;
import com.payvance.erp_saas.core.entity.VendorActivationBatch;

@Repository
public interface VendorActivationBatchRepository
        extends JpaRepository<VendorActivationBatch, Long>, JpaSpecificationExecutor<VendorActivationBatch> {
    List<VendorActivationBatch> findByVendorId(Long vendorId);
    
    /*
     * Fetch paginated Vendor Activation Batches with selected fields
     */
    @Query("""
            SELECT new com.payvance.erp_saas.core.dto.VendorActivationBatchResponseDTO(
                v.id,
                v.createdAt,
                v.plan.id,
                v.totalActivations,
                v.costPrice,
                v.status,
                v.usedActivations
            )
            FROM VendorActivationBatch v
            """)
        Page<VendorActivationBatchResponseDTO> findAllBatches(Pageable pageable);
    
    /*
     * Sum of cost_price from vendor activation batches by status
     */
    @Query("SELECT COALESCE(SUM(v.costPrice), 0) FROM VendorActivationBatch v WHERE v.status = :status")
    java.math.BigDecimal sumCostPriceByStatus(@Param("status") String status);
    
}

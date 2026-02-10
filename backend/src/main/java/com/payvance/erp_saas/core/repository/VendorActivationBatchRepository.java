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
import java.util.Map;

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
    
    /*
     * Count vendor activation batches by vendor ID and status
     */
    @Query("SELECT COUNT(v) FROM VendorActivationBatch v WHERE v.vendorId = :vendorId AND v.status = :status")
    Long countByVendorIdAndStatus(@Param("vendorId") Long vendorId, @Param("status") String status);
    
    /*
     * Sum of total_activations by vendor ID and status
     */
    @Query("SELECT COALESCE(SUM(v.totalActivations), 0) FROM VendorActivationBatch v WHERE v.vendorId = :vendorId AND v.status = :status")
    Long sumTotalActivationsByVendorIdAndStatus(@Param("vendorId") Long vendorId, @Param("status") String status);
    
    /*
     * Sum of used_activations by vendor ID and status
     */
    @Query("SELECT COALESCE(SUM(v.usedActivations), 0) FROM VendorActivationBatch v WHERE v.vendorId = :vendorId AND v.status = :status")
    Long sumUsedActivationsByVendorIdAndStatus(@Param("vendorId") Long vendorId, @Param("status") String status);
    
    /*
     * Sum of cost_price and resale_price by vendor ID and status (single query)
     */
    @Query("SELECT new map(COALESCE(SUM(v.costPrice), 0) as totalCostPrice, COALESCE(SUM(v.resalePrice), 0) as totalResalePrice) FROM VendorActivationBatch v WHERE v.vendorId = :vendorId AND v.status = :status")
    Map<String, java.math.BigDecimal> sumCostAndResalePriceByVendorIdAndStatus(@Param("vendorId") Long vendorId, @Param("status") String status);
    
    /*
     * Find 5 most recent batches by vendor ID with plan details
     */
    @Query("""
        SELECT new map(
            v.id as batchId,
            p.name as planName,
            v.totalActivations as totalActivations,
            v.status as status,
            v.createdAt as createdAt
        )
        FROM VendorActivationBatch v
        JOIN v.plan p
        WHERE v.vendorId = :vendorId
        ORDER BY v.createdAt DESC
        """)
    List<Map<String, Object>> findRecentBatchesByVendorId(@Param("vendorId") Long vendorId);
    
}

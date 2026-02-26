package com.payvance.erp_saas.core.repository;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.core.entity.TenantActivation;

@Repository
public interface TenantActivationRepository extends JpaRepository<TenantActivation, Long> {
    boolean existsByTenantIdAndStatus(Long tenantId, String status);
    java.util.List<com.payvance.erp_saas.core.entity.TenantActivation> findByTenantIdAndStatus(Long tenantId, String status);
    Optional<TenantActivation> findFirstByTenantIdOrderByCreatedAtDesc(Long tenantId);

    /*
     * Top 5 tenants by revenue (activationPrice) for a given vendor
     * Joins TenantActivation -> VendorActivationBatch on vendorBatchId,
     * then joins Tenant to get name/email.
     */
    @Query("""
        SELECT new map(
            t.name as tenantName,
            t.email as tenantEmail,
            COALESCE(SUM(ta.activationPrice), 0) as revenue,
            COUNT(ta.id) as activations
        )
        FROM TenantActivation ta
        JOIN Tenant t ON t.id = ta.tenantId
        JOIN VendorActivationBatch vab ON vab.id = ta.vendorBatchId
        WHERE vab.vendorId = :vendorId
        GROUP BY ta.tenantId, t.name, t.email
        ORDER BY SUM(ta.activationPrice) DESC
        """)
    List<Map<String, Object>> findTopTenantsByVendorId(@Param("vendorId") Long vendorId);
}

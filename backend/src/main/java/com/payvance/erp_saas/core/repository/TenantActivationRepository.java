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
    @org.springframework.data.jpa.repository.Query("""
        SELECT FUNCTION('MONTH', t.activatedAt) as month, SUM(t.activationPrice) as revenue
        FROM TenantActivation t
        WHERE LOWER(t.status) = 'active'
          AND t.activatedAt BETWEEN :startDate AND :endDate
        GROUP BY FUNCTION('MONTH', t.activatedAt)
    """)
    java.util.List<Object[]> findMonthlyRevenue(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(t.activationPrice), 0) FROM TenantActivation t WHERE LOWER(t.status) = LOWER(:status)")
    java.math.BigDecimal sumActivationPriceByStatus(@org.springframework.data.repository.query.Param("status") String status);

    @org.springframework.data.jpa.repository.Query("""
        SELECT COUNT(t.id) 
        FROM TenantActivation t 
        WHERE LOWER(t.status) = 'active' 
          AND t.activatedAt BETWEEN :startDate AND :endDate
          AND NOT EXISTS (
              SELECT 1 FROM TenantActivation t2 
              WHERE t2.tenantId = t.tenantId 
                AND LOWER(t2.status) = 'active' 
                AND t2.id < t.id
          )
    """)
    Long countNewSales(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);

    @org.springframework.data.jpa.repository.Query("""
        SELECT COUNT(t.id) 
        FROM TenantActivation t 
        WHERE LOWER(t.status) = 'active' 
          AND t.activatedAt BETWEEN :startDate AND :endDate
          AND EXISTS (
              SELECT 1 FROM TenantActivation t2 
              WHERE t2.tenantId = t.tenantId 
                AND LOWER(t2.status) = 'active' 
                AND t2.id < t.id
          )
    """)
    Long countRenewals(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);

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

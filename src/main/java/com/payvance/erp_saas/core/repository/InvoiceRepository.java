package com.payvance.erp_saas.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.core.entity.Invoice;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for Invoice entity
 *
 * @author system
 * @version 1.0.0
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /*
     * Sum of total_payable from invoices by status
     */
    @Query("SELECT COALESCE(SUM(i.totalPayable), 0) FROM Invoice i WHERE i.status = :status")
    BigDecimal sumTotalPayableByStatus(@Param("status") String status);

    /**
     * Find all invoices by subscription ID
     */
    List<Invoice> findBySubscriptionId(Long subscriptionId);

    @Query("""
        SELECT FUNCTION('MONTH', i.paidAt) as month, SUM(i.totalPayable) as revenue
        FROM Invoice i
        WHERE i.status = 'paid' 
          AND ((FUNCTION('YEAR', i.paidAt) = :startYear AND FUNCTION('MONTH', i.paidAt) >= 4)
               OR (FUNCTION('YEAR', i.paidAt) = :endYear AND FUNCTION('MONTH', i.paidAt) <= 3))
        GROUP BY FUNCTION('MONTH', i.paidAt)
    """)
    List<Object[]> findMonthlyRevenue(@Param("startYear") Integer startYear, @Param("endYear") Integer endYear);
    @Query("""
        SELECT i.tenantId, SUM(i.totalPayable) as revenue
        FROM Invoice i
        WHERE i.status = 'paid'
        GROUP BY i.tenantId
        ORDER BY revenue DESC
    """)
    List<Object[]> findTopTenantsByRevenue();
}

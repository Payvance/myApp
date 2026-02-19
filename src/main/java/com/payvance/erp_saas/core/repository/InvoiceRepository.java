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
}

package com.payvance.erp_saas.core.repository;

import com.payvance.erp_saas.core.dto.BankTransferRequestResponse;
import com.payvance.erp_saas.core.entity.BankTransferRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository for bank transfer settlement data
 */
public interface BankTransferRequestRepository
        extends JpaRepository<BankTransferRequest, Long> {

    /**
     * Fetch bank transfer settlements for a specific tenant
     * tenantId is mapped with bank_transfer_requests.tenant_id
     */
    @Query("""
        SELECT new com.payvance.erp_saas.core.dto.BankTransferRequestResponse(
            b.createdAt,
            COUNT(r.id),
            b.amount,
            b.status,
            b.utrNumber,
            b.payerBank,
            b.paidAmount
        )
        FROM BankTransferRequest b
        LEFT JOIN Referral r 
            ON r.referredTenantId = b.tenantId
        WHERE b.tenantId = :tenantId
          AND (:status IS NULL OR b.status = :status)
        GROUP BY b.id
        ORDER BY b.createdAt DESC
    """)
    Page<BankTransferRequestResponse> fetchBankTransfers(
            @Param("tenantId") Long tenantId,
            @Param("status") String status,
            Pageable pageable
    );


    /**
     * Fetch all bank transfer settlements for Super Admin
     */
        @Query("""
        SELECT new com.payvance.erp_saas.core.dto.BankTransferRequestResponse(
            b.createdAt,
            b.referralsCount,
            b.amount,
            b.status,
            b.utrNumber,
            b.payerBank,
            b.paidAmount
        )
        FROM BankTransferRequest b
        WHERE (:status IS NULL OR b.status = :status)
        ORDER BY b.createdAt DESC
    """)
    Page<BankTransferRequestResponse> fetchAllBankTransfers(
            @Param("status") String status,
            Pageable pageable
    );

    Optional<BankTransferRequest> findById(Long id);

    /**
     * Update bank transfer settlement details upon approval
     */
    @Modifying
    @Query("""
        UPDATE BankTransferRequest b
        SET b.status = :status,
            b.paymentMode = :paymentMode,
            b.utrNumber = :utrNumber,
            b.payerBank = :payerBank,
            b.paidAmount = :paidAmount,
            b.paidDate = :paidDate
        WHERE b.id = :bankTransferId
    """)
    void updateSettlementApproval(
            @Param("bankTransferId") Long bankTransferId,
            @Param("status") String status,
            @Param("paymentMode") String paymentMode,
            @Param("utrNumber") String utrNumber,
            @Param("payerBank") String payerBank,
            @Param("paidAmount") BigDecimal paidAmount,
            @Param("paidDate") LocalDate paidDate
    );


}

package com.payvance.erp_saas.core.repository;

import com.payvance.erp_saas.core.dto.ReferralResponse;
import com.payvance.erp_saas.core.dto.SettlementReferralResponse;
import com.payvance.erp_saas.core.entity.Referral;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Repository for fetching referral data
 */
public interface ReferralRepository extends JpaRepository<Referral, Long> {

    /**
     * Fetch referrals for a specific referrer (CA)
     */
    @Query("""
                SELECT new com.payvance.erp_saas.core.dto.ReferralResponse(
                    r.referredTenantId,
                    u.name,
                    r.rewardedAmount,
                    r.status,
                    r.createdAt
                )
                FROM Referral r
                JOIN User u ON u.id = r.referredTenantId
                WHERE r.referrerId = :referrerId
                  AND (:status IS NULL OR r.status = :status)
                  AND (:referredTenantId IS NULL OR r.referredTenantId = :referredTenantId)
                  AND (:fromDate IS NULL OR r.createdAt >= :fromDate)
                  AND (:toDate IS NULL OR r.createdAt <= :toDate)
                ORDER BY r.createdAt DESC
            """)
    Page<ReferralResponse> findReferralData(
            Long referrerId,
            String status,
            Long referredTenantId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable);

    /**
     * Update bank_transfer_id for selected referrals
     */
    @Modifying
    @Query("""
            UPDATE Referral r
            SET r.bankTransferId = :bankTransferId,
                r.status = :status
            WHERE r.id IN :referralIds
                  """)
    void updateBankTransferId(
            @Param("bankTransferId") Long bankTransferId,
            @Param("status") String status,
            @Param("referralIds") List<Long> referralIds
    );
   /**
     * Fetch all referrals (for Super Admin)
     */ 
    @Query("""
        SELECT new com.payvance.erp_saas.core.dto.ReferralResponse(
            r.referredTenantId,
            u.name,
            r.rewardedAmount,
            r.status,
            r.createdAt
        )
        FROM Referral r
        JOIN User u ON u.id = r.referredTenantId
        WHERE (:status IS NULL OR r.status = :status)
        AND (:referredTenantId IS NULL OR r.referredTenantId = :referredTenantId)
        AND (:fromDate IS NULL OR r.createdAt >= :fromDate)
        AND (:toDate IS NULL OR r.createdAt <= :toDate)
        ORDER BY r.createdAt DESC
    """)
    Page<ReferralResponse> findAllReferralData(
            String status,
            Long referredTenantId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    );

    @Query("""
    SELECT new com.payvance.erp_saas.core.dto.SettlementReferralResponse(
        r.id,
        r.referredTenantId,
        r.rewardedAmount,
        r.status,
        r.createdAt
    )
    FROM Referral r
    WHERE r.bankTransferId = :bankTransferId
    """)
    List<SettlementReferralResponse> findAllByBankTransferId(
            @Param("bankTransferId") Long bankTransferId
    );

    /**
     * Update referral status by bank transfer ID
     */
    @Modifying
    @Query("""
        UPDATE Referral r
        SET r.status = :status
        WHERE r.bankTransferId = :bankTransferId
    """)
    void updateStatusByBankTransferId(
            @Param("bankTransferId") Long bankTransferId,
            @Param("status") String status
    );


}

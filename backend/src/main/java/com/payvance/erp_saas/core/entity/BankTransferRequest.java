package com.payvance.erp_saas.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "bank_transfer_requests")
@Getter
@Setter
public class BankTransferRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "amount")
    private BigDecimal amount;

     // Payment mode (BANK_TRANSFER, UPI, etc.)
    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name = "referals_count", nullable = false)
    private Long referralsCount;

    @Column(name = "utr_number")
    private String utrNumber;

    @Column(name = "payer_bank")
    private String payerBank;

    @Column(name = "paid_amount")
    private BigDecimal paidAmount;

      @Column(name = "paid_date")
    private LocalDateTime paidDate;

    
    // Super admin verification
    @Column(name = "verified_by_user_id")
    private Long verifiedByUserId;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    // Status: PENDING, REQUEST_RAISED, APPROVED, REJECTED
    @Column(name = "status", nullable = false)
    private String status;

    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

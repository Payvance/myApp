package com.payvance.erp_saas.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing referral rewards
 */
@Entity
@Table(name = "referrals")
@Getter
@Setter
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "referrer_tenant_id", nullable = false)
    private Long referrerTenantId;

    @Column(name = "referred_tenant_id", nullable = false)
    private Long referredTenantId;

    @Column(name = "rewarded_amount", nullable = false)
    private BigDecimal rewardedAmount;

    @Column(nullable = false)
    private String status;

    @Column(name = "referrer_id")
    private Long referrerId;

    @Column(name = "bank_transfer_id")
    private Long bankTransferId;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

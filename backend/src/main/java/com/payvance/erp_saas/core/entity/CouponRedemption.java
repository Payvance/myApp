package com.payvance.erp_saas.core.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "coupon_redemptions")
@Getter
@Setter
public class CouponRedemption {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(name = "coupon_id", nullable = false)
	    private Long couponId;

	    @Column(name = "tenant_id", nullable = false)
	    private Long tenantId;

	    @Column(name = "invoice_id")
	    private Long invoiceId;

	    @Column(
	            name = "discount_applied",
	            nullable = false,
	            precision = 12,
	            scale = 2
	    )
	    private BigDecimal discountApplied = BigDecimal.ZERO;

	    @Column(name = "redeemed_at")
	    private LocalDateTime redeemedAt;

	    @Column(name = "created_at")
	    private LocalDateTime createdAt;

	    @Column(name = "updated_at")
	    private LocalDateTime updatedAt;

	    
}

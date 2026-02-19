package com.payvance.erp_saas.core.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing Invoice information
 *
 * @author system
 * @version 1.0.0
 */
@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoice {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "subscription_id")
    private Long subscriptionId;

    @Column(name = "invoice_number", nullable = false, length = 80)
    private String invoiceNumber;

    @Column(name = "gateway", length = 50)
    private String gateway;

    @Column(
        name = "subtotal",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(
        name = "discount_total",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal discountTotal = BigDecimal.ZERO;

    @Column(name = "discount_by", length = 60)
    private String discountBy;

    @Column(
        name = "total_payable",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal totalPayable = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "INR";

    @Column(name = "status", nullable = false, length = 30)
    private String status = "unpaid";

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "gateway_payment_id", length = 120)
    private String gatewayPaymentId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ---------------------------
    // Auto timestamps
    // ---------------------------
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}

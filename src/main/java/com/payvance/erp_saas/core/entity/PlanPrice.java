package com.payvance.erp_saas.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing pricing details for a Plan.
 * Includes billing period, currency, and amount.
 *
 * @author Aniket Desai
 */
@Entity
@Table(name = "plan_prices")
@Getter
@Setter
public class PlanPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "plan_id",
        nullable = false,
        unique = true
    )
    private Plan plan;

    @Column(name = "billing_period", length = 30, nullable = false)
    private String billingPeriod;

    @Column(name = "currency", length = 10, nullable = false)
    private String currency = "INR";

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;
    
	@Column(name = "period_value", nullable = false)
	private Integer duration = 1;

    @Column(name = "is_active", nullable = false)
    private Byte isActive = 1;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

package com.payvance.erp_saas.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "discount_type", nullable = false)
    private String discountType; // FLAT / PERCENTAGE

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "discount_value")
    private Double discountValue;

    @Column(length = 10)
    private String currency;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "max_uses")
    private Long maxUses;

    @Column(name = "used_count")
    private Long usedCount = 0L;

    @Column(length = 1000)
    private String discription;

    @Column(length = 20)
    private String status; // ACTIVE / INACTIVE

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.usedCount == null) {
            this.usedCount = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

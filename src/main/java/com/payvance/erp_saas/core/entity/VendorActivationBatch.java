/**
 * Copyright: Â© 2024 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of Payvance Innovation Pvt. Ltd.
 * Payvance Innovation Pvt. Ltd. retains all title to and intellectual property rights in these materials.
 *
 **/

/**
 *
 * @author           version     date        change description
 * Aniket Desai  	 1.0.0       06-Jan-2026    class created
 *
 **/
package com.payvance.erp_saas.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_activation_batches")
@Getter
@Setter
public class VendorActivationBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "license_model_id", nullable = false)
    private Long licenseModelId;

    @Column(name = "vendor_discount_id")
    private Integer vendorDiscountId;

    @Column(name = "total_activations", nullable = false)
    private Integer totalActivations = 0;

    @Column(name = "used_activations", nullable = false)
    private Integer usedActivations = 0;

    @Column(name = "cost_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal costPrice = BigDecimal.ZERO;

    @Column(name = "resale_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal resalePrice = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "INR";

    @Column(name = "status", nullable = false, length = 30)
    private String status = "active";

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "issued_by_user_id")
    private Long issuedByUserId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

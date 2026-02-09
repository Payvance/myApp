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

import java.time.LocalDateTime;

@Entity
@Table(name = "activation_keys")
@Getter
@Setter
public class ActivationKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_batch_id", nullable = false)
    private Long vendorBatchId;

    @Column(name = "activation_code_hash", nullable = false, unique = true)
    private String activationCodeHash;

    @Column(name = "plain_code_last4", length = 4)
    private String plainCodeLast4;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.UNUSED;

    @Column(name = "issued_to_email")
    private String issuedToEmail;

    @Column(name = "issued_to_phone", length = 30)
    private String issuedToPhone;

    @Column(name = "redeemed_tenant_id")
    private Long redeemedTenantId;

    @Column(name = "redeemed_at")
    private LocalDateTime redeemedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Status {
        UNUSED, ISSUED, REDEEMED, EXPIRED, REVOKED
    }
    
    @Transient
    private String redeemedTenantName;
}

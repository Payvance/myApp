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
 * om            	 1.0.0       05-Jan-2026    class created
 *
 **/
package com.payvance.erp_saas.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
    name = "wallets",
    uniqueConstraints = @UniqueConstraint(columnNames = {"ownerType", "ownerId"})
)
@Getter
@Setter
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ownerType; // vendor | ca | tenantAdmin

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String currency = "INR";

    @Column(nullable = false)
    private Double balance = 0.0;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

	
}

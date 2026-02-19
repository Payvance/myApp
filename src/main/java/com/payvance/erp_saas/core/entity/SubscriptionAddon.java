package com.payvance.erp_saas.core.entity;

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
@Table(name = "subscription_addons")
@Getter
@Setter
public class SubscriptionAddon {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(name = "addon_id", nullable = false)
    private Long addonId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "pending";

    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;

    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

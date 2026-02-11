package com.payvance.erp_saas.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity for CA-Tenant relationship
 * 
 * @author system
 * @version 1.0.0
 */
@Entity
@Table(name = "ca_tenants")
@Getter
@Setter
public class CaTenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "ca_id", nullable = false)
    private Long caId;

    @Column(name = "is_view")
    private Integer isView = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

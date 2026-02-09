package com.payvance.erp_saas.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing limitations associated with a Plan.
 * Defines allowed user and company counts.
 *
 * @author Aniket Desai
 */
@Entity
@Table(name = "plan_limitations")
@Getter
@Setter
public class PlanLimitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "plan_id", nullable = false, unique = true)
    private Plan plan;

    @Column(name = "allowed_user_count", nullable = false)
    private Integer allowedUserCount = 1;

    @Column(name = "allowed_company_count", nullable = false)
    private Integer allowedCompanyCount = 1;

    @Column(name = "allowed_user_count_till", nullable = false)
    private Integer allowedUserCountTill = 0;

    @Column(name = "allowed_company_count_till", nullable = false)
    private Integer allowedCompanyCountTill = 0;

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

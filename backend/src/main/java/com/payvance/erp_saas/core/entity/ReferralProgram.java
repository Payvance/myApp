package com.payvance.erp_saas.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "referral_programs")
@Getter
@Setter
public class ReferralProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;
    
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "owner_type", nullable = false)
    private String ownerType; // CA / TENANT

    @Column(nullable = false, length = 1000)
    private String name;

    @Column(name = "reward_type", nullable = false)
    private String rewardType; // FLAT / PERCENTAGE

    @Column(name = "reward_value")
    private Double rewardValue; // for FLAT

    @Column(name = "reward_percentage")
    private Double rewardPercentage; // for PERCENTAGE

    @Column(name = "reward_trigger")
    private String rewardTrigger;

    @Column(name = "max_per_referrer")
    private Double maxPerReferrer;

    @Column(nullable = false)
    private String status; // ACTIVE / INACTIVE

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

	public Long getProgramId() {
		// TODO Auto-generated method stub
		return null;
	}
}

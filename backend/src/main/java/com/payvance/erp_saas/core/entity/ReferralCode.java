package com.payvance.erp_saas.core.entity;

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

@Entity
@Table(name = "referral_codes")
@Getter
@Setter
public class ReferralCode {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(name = "program_id", nullable = false)
	    private Long programId;

	    @Column(name = "owner_type", length = 30, nullable = false)
	    private String ownerType;

	    @Column(name = "owner_id", nullable = false)
	    private Long ownerId;

	    @Column(name = "code", length = 60, nullable = false, unique = true)
	    private String code;

	    @Column(name = "max_uses", nullable = false)
	    private Integer maxUses = 0;

	    @Column(name = "used_count", nullable = false)
	    private Integer usedCount = 0;

	    @Column(name = "status", length = 30, nullable = false)
	    private String status = "active";

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

		public Long getTenantId() {
			// TODO Auto-generated method stub
			return null;
		}
}

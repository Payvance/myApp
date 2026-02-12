package com.payvance.erp_saas.core.entity;

import java.math.BigDecimal;
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
@Table(
    name = "tenant_activations")
@Getter
@Setter
public class TenantActivation {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(name = "tenant_id", nullable = false)
	    private Long tenantId;

	    @Column(name = "license_model_id")
	    private Long licenseModelId;

	    @Column(name = "source", length = 50)
	    private String source;

	    @Column(name = "vendor_batch_id")
	    private Long vendorBatchId;

	    @Column(name = "referral_id")
	    private Long referralId;

	    @Column(name = "activation_code_hash", length = 191)
	    private String activationCodeHash;

	    @Column(
	        name = "activation_price",
	        nullable = false,
	        precision = 10,
	        scale = 2
	    )
	    private BigDecimal activationPrice = BigDecimal.ZERO;

	    @Column(name = "currency", length = 10, nullable = false)
	    private String currency = "INR";

	    @Column(name = "activated_at")
	    private LocalDateTime activatedAt;

	    @Column(name = "expires_at")
	    private LocalDateTime expiresAt;

	    @Column(name = "status", length = 30, nullable = false)
	    private String status = "pending";

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

		public void save(TenantActivation activation) {
			// TODO Auto-generated method stub
			
		}

}

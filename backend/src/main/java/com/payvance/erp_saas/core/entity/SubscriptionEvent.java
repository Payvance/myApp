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
@Table(name = "subscription_events")
@Getter
@Setter
public class SubscriptionEvent {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(name = "tenant_id", nullable = false)
	    private Long tenantId;

	    @Column(name = "subscription_id", nullable = false)
	    private Long subscriptionId;

	    @Column(name = "event_type", nullable = false, length = 60)
	    private String eventType;

	    @Column(name = "from_plan_id")
	    private Long fromPlanId;

	    @Column(name = "to_plan_id")
	    private Long toPlanId;

	    /**
	     * Stores raw event payload (MySQL JSON)
	     * Can be mapped to DTO later if needed
	     */
	    @Column(name = "payload_json", columnDefinition = "json")
	    private String payloadJson;

	    @Column(name = "received_at")
	    private LocalDateTime receivedAt;

	    @Column(name = "processed_at")
	    private LocalDateTime processedAt;

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

}

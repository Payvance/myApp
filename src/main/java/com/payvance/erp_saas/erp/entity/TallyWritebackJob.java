package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tally_writeback_jobs")
@Data
public class TallyWritebackJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_id", nullable = false)
    private String entityId; // GUID or Local ID

    @Column(name = "entity_type", nullable = false)
    private String entityType; // VOUCHER, LEDGER, ITEM, etc.

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload; // Optional: Pre-computed XML or JSON

    @Column(name = "status", nullable = false)
    private String status; // PENDING, IN_PROGRESS, COMPLETED, FAILED

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null)
            status = "PENDING";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

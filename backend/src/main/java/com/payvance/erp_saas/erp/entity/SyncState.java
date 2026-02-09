package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "sync_state", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "tenant_id", "company_id" })
})
@Data
public class SyncState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @Column(name = "last_alter_id")
    private Long lastAlterId;

    @Column(name = "last_sync_time")
    private LocalDateTime lastSyncTime;
}

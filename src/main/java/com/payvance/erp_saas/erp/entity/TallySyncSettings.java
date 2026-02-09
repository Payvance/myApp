package com.payvance.erp_saas.erp.entity;

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
@Table(name = "tally_sync_settings")
@Getter
@Setter
public class TallySyncSettings {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "tenant_id", nullable = false)
	private Long tenantId;

	// ---------------- SYNC TAB ----------------

	@Column(name = "sync_interval_minutes", nullable = false)
	private Integer syncIntervalMinutes = 5;

	@Column(name = "enable_deleted_voucher_sync", nullable = false)
	private Boolean enableDeletedVoucherSync = false;

	@Column(name = "deleted_voucher_sync_minutes")
	private Integer deletedVoucherSyncMinutes = 60;

	@Column(name = "profit_loss_sync_hours", nullable = false)
	private Integer profitLossSyncHours = 24;

	@Column(name = "tally_retry_attempts", nullable = false)
	private Integer tallyRetryAttempts = 2;

	// ---------------- ADDITIONAL TAB ----------------

	@Column(name = "enable_notifications", nullable = false)
	private Boolean enableNotifications = true;

	@Column(name = "enable_chunking", nullable = false)
	private Boolean enableChunking = false;

	@Column(name = "chunk_size")
	private Integer chunkSize = 3000;

	@Column(name = "enable_master_size_chunk", nullable = false)
	private Boolean enableMasterSizeChunk = false;

	@Column(name = "no_admin_access", nullable = false)
	private Boolean noAdminAccess = false;

	@Column(name = "enable_proxy", nullable = false)
	private Boolean enableProxy = false;

	@Column(name = "proxy_url")
	private String proxyUrl;

	// ---------------- AUDIT ----------------

	@Column(name = "created_at", updatable = false)
	private java.time.LocalDateTime createdAt;

	@Column(name = "updated_at")
	private java.time.LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = java.time.LocalDateTime.now();
		updatedAt = java.time.LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = java.time.LocalDateTime.now();
	}
}

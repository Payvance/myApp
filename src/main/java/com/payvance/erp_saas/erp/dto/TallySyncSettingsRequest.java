package com.payvance.erp_saas.erp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TallySyncSettingsRequest {
	
	private Integer syncIntervalMinutes;
    private Boolean enableDeletedVoucherSync;
    private Integer deletedVoucherSyncMinutes;
    private Integer profitLossSyncHours;
    private Integer tallyRetryAttempts;
    private Boolean enableNotifications;
    private Boolean enableChunking;
    private Integer chunkSize;
    private Boolean enableMasterSizeChunk;
    private Boolean noAdminAccess;
    private Boolean enableProxy;
    private String proxyUrl;
}
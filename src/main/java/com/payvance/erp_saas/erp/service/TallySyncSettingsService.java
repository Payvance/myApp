package com.payvance.erp_saas.erp.service;

import org.springframework.stereotype.Service;

import com.payvance.erp_saas.erp.dto.TallySyncSettingsRequest;
import com.payvance.erp_saas.erp.entity.TallySyncSettings;
import com.payvance.erp_saas.erp.repository.TallySyncSettingsRepository;
import com.payvance.erp_saas.erp.security.TenantContext;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TallySyncSettingsService {

    private final TallySyncSettingsRepository repository;

    @Transactional
    public TallySyncSettings saveOrUpdate(TallySyncSettingsRequest request) {
        // Resolve Tenant ID from the Global Context
        Long tenantId = TenantContext.getCurrentTenant();

        if (tenantId == null) {
            throw new RuntimeException("Unauthorized: Tenant ID is missing from context.");
        }

        // Fetch the existing record or initialize a new one
        TallySyncSettings settings = repository.findByTenantId(tenantId)
                .orElseGet(() -> {
                    TallySyncSettings newEntry = new TallySyncSettings();
                    newEntry.setTenantId(tenantId);
                    return newEntry;
                });

        // Update Numeric and Boolean Fields (Ignore if Null)
        if (request.getSyncIntervalMinutes() != null)
            settings.setSyncIntervalMinutes(request.getSyncIntervalMinutes());
        if (request.getEnableDeletedVoucherSync() != null)
            settings.setEnableDeletedVoucherSync(request.getEnableDeletedVoucherSync());
        if (request.getDeletedVoucherSyncMinutes() != null)
            settings.setDeletedVoucherSyncMinutes(request.getDeletedVoucherSyncMinutes());
        if (request.getProfitLossSyncHours() != null)
            settings.setProfitLossSyncHours(request.getProfitLossSyncHours());
        if (request.getTallyRetryAttempts() != null)
            settings.setTallyRetryAttempts(request.getTallyRetryAttempts());
        if (request.getEnableNotifications() != null)
            settings.setEnableNotifications(request.getEnableNotifications());
        if (request.getEnableChunking() != null)
            settings.setEnableChunking(request.getEnableChunking());
        if (request.getChunkSize() != null)
            settings.setChunkSize(request.getChunkSize());
        if (request.getEnableMasterSizeChunk() != null)
            settings.setEnableMasterSizeChunk(request.getEnableMasterSizeChunk());
        if (request.getNoAdminAccess() != null)
            settings.setNoAdminAccess(request.getNoAdminAccess());
        if (request.getEnableProxy() != null)
            settings.setEnableProxy(request.getEnableProxy());

        // Update String Fields (Ignore if Null or Blank/Whitespace)
        String updatedProxyUrl = processString(request.getProxyUrl());
        if (updatedProxyUrl != null) {
            settings.setProxyUrl(updatedProxyUrl);
        }

        return repository.save(settings);
    }

    private String processString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        return input.trim();
    }

    public TallySyncSettings getSettings() {
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new RuntimeException("Unauthorized: Tenant ID is missing from context.");
        }
        return repository.findByTenantId(tenantId)
                .orElseGet(() -> {
                    TallySyncSettings newEntry = new TallySyncSettings();
                    newEntry.setTenantId(tenantId);
                    return newEntry;
                });
    }
}

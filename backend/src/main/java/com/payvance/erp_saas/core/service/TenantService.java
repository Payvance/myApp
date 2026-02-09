/**
 * Copyright: Â© 2024 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of Payvance Innovation Pvt. Ltd.
 * Payvance Innovation Pvt. Ltd. retains all title to and intellectual property rights in these materials.
 *
 **/

/**
 *
 * @author           version     date        change description
 * om            	 1.0.0       05-Jan-2026    class created
 *
 **/
package com.payvance.erp_saas.core.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payvance.erp_saas.core.config.TrialConfig;
import com.payvance.erp_saas.core.dto.LicenseCheckDto;
import com.payvance.erp_saas.core.dto.TrialPeriod;
import com.payvance.erp_saas.core.entity.Tenant;
import com.payvance.erp_saas.core.entity.TenantSetting;
import com.payvance.erp_saas.core.entity.TenantUsage;
import com.payvance.erp_saas.core.entity.User;
import com.payvance.erp_saas.core.entity.Vendor;
import com.payvance.erp_saas.core.repository.TenantRepository;
import com.payvance.erp_saas.core.repository.TenantSettingsRepository;
import com.payvance.erp_saas.core.repository.TenantUsageRepository;
import com.payvance.erp_saas.core.repository.TenantUserRoleRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
import com.payvance.erp_saas.core.repository.VendorRepository;
import com.payvance.erp_saas.exceptions.UserNotAllowedException;
import com.payvance.erp_saas.exceptions.UserNotFoundException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final TenantSettingsRepository tenantSettingsRepository;
    private final TenantUsageRepository tenantUsageRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final VendorRepository vendorRepository;
    private final EmailService emailService;
    private final EventService eventService;
    private final TrialConfig trialConfig;

    @Transactional
    public TrialPeriod startTrial(Long tenantId, Long actorUserId) {

        // Validate actor exists and email verified
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new UserNotFoundException("Actor user not found"));

        if (actor.getEmailVerifiedAt() == null) {
            throw new UserNotAllowedException("User email not verified");
        }

        // Validate actor is tenant admin for this tenant
    tenantUserRoleRepository
        .findByUserIdAndTenantIdAndRoleIdAndIsActiveTrue(actorUserId, tenantId, 2L)
        .orElseThrow(() -> new UserNotAllowedException("User is not a tenant admin"));

        // Lock tenant row
        Tenant tenant = tenantRepository.findByIdForUpdate(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found"));


        TenantSetting setting = tenantSettingsRepository.findByTenantId(tenantId)
                .orElseGet(TenantSetting::new);

        LocalDateTime now = LocalDateTime.now();

        // 🔴 CASE: Trial already started
        if (tenant.getTrialStartAt() != null) {

            LocalDateTime trialOnlyEnd =
                    tenant.getTrialStartAt().plusDays(setting.getTrialDays());

            LocalDateTime totalTrialEnd =
                    tenant.getTrialStartAt().plusDays(
                            setting.getTrialDays() + setting.getExtendedTrialDays()
                    );

            // ❌ TOTAL 14 DAYS EXPIRED
            if (now.isAfter(totalTrialEnd)) {

                tenantRepository.updateTenantStatus(
                        tenantId,
                        "payment_pending"
                );

                throw new UserNotAllowedException(
                        "Trial period expired. Please complete payment to continue."
                );
            }

            // ✅ TRIAL DAYS EXPIRED → ENABLE ADS
            if (now.isAfter(trialOnlyEnd) && !setting.isAdsUnlockedEnabled()) {
                setting.setAdsUnlockedEnabled(true);
                tenantSettingsRepository.save(setting);
            }

            throw new UserNotAllowedException("Trial already started");
        }

        // ❌ Trial allowed only for inactive tenants
        if (!"inactive".equalsIgnoreCase(tenant.getStatus())) {
            throw new UserNotAllowedException(
                    "Trial not allowed. Current status: " + tenant.getStatus()
            );
        }

        setting.setTenantId(tenantId);
        setting.setTimezone(setting.getTimezone() == null ? "UTC" : setting.getTimezone());
        setting.setCurrency(setting.getCurrency() == null ? "INR" : setting.getCurrency());
        setting.setMaxCompanies(trialConfig.getCompaniesCount());
        setting.setTrialDays(trialConfig.getTrialDays());
        setting.setExtendedTrialDays(trialConfig.getExtendedTrialDays());
        setting.setAdsUnlockedEnabled(trialConfig.isAdsUnlockedEnabled());
        setting.setAdsUnlockedDays(trialConfig.getAdsUnlockedDays());

        tenantSettingsRepository.save(setting);

        // Upsert usage baseline
        TenantUsage usage = tenantUsageRepository.findByTenantId(tenantId)
                .orElseGet(() -> new TenantUsage());

        usage.setTenantId(tenantId);
        usage.setActiveUsersCount(trialConfig.getActiveUsersCount());
        usage.setCompaniesCount(trialConfig.getCompaniesCount());

        tenantUsageRepository.save(usage);

        // Compute period
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(setting.getTrialDays() + setting.getExtendedTrialDays()); // total trial
        // Update tenant status
        tenantRepository.updateTenantStatusToTrial(tenantId, start, end);
        
       
        // Send email
        emailService.sendTrialStartedEmail(tenant.getEmail(), start, end);

        // Log event
        eventService.logEvent("tenant", tenantId, "trial_started", Map.of("start", start.toString(), "end", end.toString()), actorUserId);

        return new TrialPeriod(start, end);
    }

    /**
     * Resolve tenant_id from tenant_user_role by userId (tenant admin) and start trial.
     */
    @Transactional
    public TrialPeriod startTrialByUser(Long userId) {
        // find tenant_user_role entry for this user as tenant admin
    var tur = tenantUserRoleRepository
        .findByUserIdAndRoleIdAndIsActiveTrue(userId, 2L)
        .orElseThrow(() -> new UserNotAllowedException("Tenant mapping not found for user"));

    Long tenantId = tur.getTenantId();
        return startTrial(tenantId, userId);
    }
    
    
    /*
     * * Check eligibility for getting license based on email and phone
     */
    public LicenseCheckDto check(LicenseCheckDto dto) {
        List<Object[]> result = tenantRepository.checkEligibility(dto.getEmail(), dto.getPhone());

        // Find vendor by userId to get vendorId
        Optional<Vendor> vendor = vendorRepository.findByUserId(dto.getUserId());
        Long vendorId = vendor.map(v -> v.getId()).orElse(null);

        if (!result.isEmpty()) {
        	  Object[] row = result.get(0); 
            Long tenantId = ((Number) row[0]).longValue();
            String tenantName = (String) row[1];
            String tenantEmail = (String) row[2]; // email from query
            String tenantPhone = (String) row[3]; // phone from query

            dto.setEligible(true);
            dto.setTenantId(tenantId);
            dto.setTenantName(tenantName);
            dto.setTenantEmail(tenantEmail); // set email
            dto.setTenantPhone(tenantPhone); // set phone
            dto.setVendorId(vendorId); // set vendorId from vendor lookup
            dto.setMessage("Eligible to get license");
        } else {
            dto.setEligible(false);
            dto.setTenantId(null);
            dto.setTenantName(null);
            dto.setTenantEmail(null); // clear email
            dto.setTenantPhone(null); // clear phone
            dto.setVendorId(vendorId); // still set vendorId even if not eligible
            dto.setMessage("Not applicable to get license");
        }

        return dto;
    }
    
    
    /*
     * To get tenant by id
     */
    public Tenant getTenantById(Long tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Tenant not found with id: " + tenantId));
    }
    
    
    /*
     * To get trial config
     */
    public TrialConfig getTrialConfig() {
    	TrialConfig res = new TrialConfig();
        res.setTrialDays(trialConfig.getTrialDays());
        res.setExtendedTrialDays(trialConfig.getExtendedTrialDays());
        res.setAdsUnlockedEnabled(trialConfig.isAdsUnlockedEnabled());
        res.setAdsUnlockedDays(trialConfig.getAdsUnlockedDays());
        res.setActiveUsersCount(trialConfig.getActiveUsersCount());
        res.setCompaniesCount(trialConfig.getCompaniesCount());
        return res;
    }

    /*
     * To get tenants mapped to a user by userId
     */
    public List<Tenant> getTenantsByUserId(Long userId) {

        List<Long> tenantIds = tenantUserRoleRepository
                .findTenantIdsByUserId(userId);

        if (tenantIds.isEmpty()) {
            throw new RuntimeException("No tenants mapped to userId: " + userId);
        }

        return tenantRepository.findAllById(tenantIds);
    }

}

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
import com.payvance.erp_saas.core.dto.ValidationResponse;
import com.payvance.erp_saas.core.entity.*;
import com.payvance.erp_saas.core.repository.*;
import com.payvance.erp_saas.exceptions.UserNotAllowedException;
import com.payvance.erp_saas.exceptions.UserNotFoundException;

import lombok.RequiredArgsConstructor;

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
    private final ReferralCodeRepository referralCodeRepository;
    private final TenantActivationRepository tenantActivationRepository;
    private final ActivationKeyRepository activationKeyRepository;

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

    
 /*
  * Validate referral code and linked tenant. Returns VALID if both referral code and tenant are valid, otherwise INVALID with reason.
  */
    public ValidationResponse validate(String referralCode) {

        // Validate referral code
        ReferralCode referral = referralCodeRepository
                .findByCodeAndStatus(referralCode, "ACTIVE")
                .orElse(null);

        if (referral == null) {
            return new ValidationResponse(
                    "INVALID",
                    "Invalid or expired referral code"
            );
        }

        // Validate tenant linked to referral
        Tenant tenant = tenantRepository
                .findByIdAndStatus(referral.getOwnerId(), "ACTIVE")
                .orElse(null);

        if (tenant == null) {
            return new ValidationResponse(
                    "INVALID",
                    "Tenant is inactive or does not exist"
            );
        }

        // 3️⃣ Success
        return new ValidationResponse(
                "VALID",
                "Tenant and referral code are valid"
        );
    }
    
    /*
     * Get consolidated license status for a tenant
     */
    public Map<String, Object> getLicenseStatus(Long tenantId) {
        Tenant tenant = getTenantById(tenantId);
        LocalDateTime now = LocalDateTime.now();

        // 1. Check TenantActivation table
        Optional<TenantActivation> activation = tenantActivationRepository.findFirstByTenantIdOrderByCreatedAtDesc(tenantId);
        if (activation.isPresent() && "active".equalsIgnoreCase(activation.get().getStatus())) {
            if (activation.get().getExpiresAt() != null && activation.get().getExpiresAt().isAfter(now)) {
                return Map.of(
                    "status", "ACTIVE",
                    "expiry", activation.get().getExpiresAt().toString(),
                    "message", "License is active (SaaS)"
                );
            }
        }

        // 2. Check ActivationKey table (Vendor Licenses)
        List<ActivationKey> activeKeys = activationKeyRepository.findActiveBlockingKeys(tenantId, now);
        if (!activeKeys.isEmpty()) {
            // Take the one with the latest expiry
            ActivationKey latestKey = activeKeys.stream()
                .filter(k -> k.getExpiresAt() != null)
                .max(java.util.Comparator.comparing(ActivationKey::getExpiresAt))
                .orElse(activeKeys.get(0));

            return Map.of(
                "status", "ACTIVE",
                "expiry", latestKey.getExpiresAt() != null ? latestKey.getExpiresAt().toString() : "N/A",
                "message", "License is active (Key)"
            );
        }

        // 3. Check if EXPIRED (SaaS)
        if (activation.isPresent() && activation.get().getExpiresAt() != null) {
            return Map.of(
                "status", "EXPIRED",
                "expiry", activation.get().getExpiresAt().toString(),
                "message", "License has expired"
            );
        }

        // 2. Check Trial
        if (tenant.getTrialStartAt() != null && tenant.getTrialEndAt() != null) {
            if (tenant.getTrialEndAt().isAfter(now)) {
                return Map.of(
                    "status", "trial",
                    "expiry", tenant.getTrialEndAt().toString(),
                    "message", "Trial version is active"
                );
            } else {
                return Map.of(
                    "status", "payment_pending",
                    "expiry", tenant.getTrialEndAt().toString(),
                    "message", "Trial version has expired"
                );
            }
        }

        // 3. No License
        return Map.of(
            "status", "NONE",
            "message", "No active license or trial found"
        );
    }

    /**
     * Get consolidated license status for a user (by their tenant)
     */
    public Map<String, Object> getLicenseStatusByUserId(Long userId) {
        List<Long> tenantIds = tenantUserRoleRepository.findTenantIdsByUserId(userId);
        if (tenantIds.isEmpty()) {
            return Map.of(
                "status", "NONE",
                "message", "No tenant associated with user"
            );
        }
        // Assuming the first tenant for license check
        return getLicenseStatus(tenantIds.get(0));
    }
}

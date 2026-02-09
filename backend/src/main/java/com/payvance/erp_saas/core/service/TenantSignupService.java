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
 * Anjor         	 1.0.0       28-Dec-2025    class created
 *
 **/
package com.payvance.erp_saas.core.service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payvance.erp_saas.core.config.TrialConfig;
import com.payvance.erp_saas.core.dto.TenantSignupRequest;
import com.payvance.erp_saas.core.dto.TenantTrialRequest;
import com.payvance.erp_saas.core.dto.TrialPeriod;
import com.payvance.erp_saas.core.entity.ReferralCode;
import com.payvance.erp_saas.core.entity.ReferralProgram;
import com.payvance.erp_saas.core.entity.Role;
import com.payvance.erp_saas.core.entity.Tenant;
import com.payvance.erp_saas.core.entity.TenantIntegration;
import com.payvance.erp_saas.core.entity.TenantSetting;
import com.payvance.erp_saas.core.entity.TenantUsage;
import com.payvance.erp_saas.core.entity.TenantUserRole;
import com.payvance.erp_saas.core.entity.User;
import com.payvance.erp_saas.core.repository.ReferralCodeRepository;
import com.payvance.erp_saas.core.repository.ReferralProgramRepository;
import com.payvance.erp_saas.core.repository.RoleRepository;
import com.payvance.erp_saas.core.repository.TenantIntegrationRepository;
import com.payvance.erp_saas.core.repository.TenantRepository;
import com.payvance.erp_saas.core.repository.TenantSettingsRepository;
import com.payvance.erp_saas.core.repository.TenantUsageRepository;
import com.payvance.erp_saas.core.repository.TenantUserRoleRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
import com.payvance.erp_saas.core.util.ReferralCodeUtil;
import com.payvance.erp_saas.exceptions.DuplicateEntryException;
import com.payvance.erp_saas.exceptions.UserNotAllowedException;
import com.payvance.erp_saas.exceptions.UserNotFoundException;
import com.payvance.erp_saas.security.util.JwtUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenantSignupService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final TenantIntegrationRepository tenantIntegrationRepository;
    private final TenantSettingsRepository tenantSettingsRepository;
    private final TenantUsageRepository tenantUsageRepository;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final TrialConfig trialConfig;
    private final WalletService walletService;
    private final ReferralProgramRepository referralProgramRepository;
    private final ReferralCodeRepository referralCodeRepository;
    private final RoleRepository roleRepository;
    

    @Transactional
    public void registerTenant(TenantSignupRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateEntryException("Email already registered");
        }

        try {
            // Step 1: Create User
            User user = new User();
            user.setName(req.getName());
            user.setEmail(req.getEmail());
            user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
            user.setPhone(req.getPhone());
            user.setActive(true);
            user.setSuperadmin(false);
            userRepository.save(user);

            // Step 2: Create Tenant
            Tenant tenant = new Tenant();
            tenant.setName(req.getName());
            tenant.setEmail(req.getEmail());
            tenant.setPhone(req.getPhone());
            tenant.setStatus("inactive");
            tenantRepository.save(tenant);
            
            walletService.createWalletIfNotExists(
                    tenant,
                    tenant.getId()
            );

            // Step 3: Map User ↔ Tenant
            TenantUserRole tur = new TenantUserRole();
            tur.setTenantId(tenant.getId());
            tur.setUserId(user.getId());
            tur.setRoleId(2L); // TENANT_ADMIN
            tur.setIsActive(true);
            tenantUserRoleRepository.save(tur);

            // Step 4: Default Integration
            TenantIntegration ti = new TenantIntegration();
            ti.setTenantId(tenant.getId());
            ti.setIntegrationId(1L);
            ti.setStatus("active");
            tenantIntegrationRepository.save(ti);

            
         // Fetch ACTIVE referral program for tenant role 
            ReferralProgram tenantProgram = referralProgramRepository
                    .findTopByStatusAndRoleIdOrderByCreatedAtDesc("ACTIVE", 2L)
                    .orElseThrow(() -> new RuntimeException("No active referral program found for TENANT_ADMIN"));

            // Resolve role dynamically
            Role tenantRole = roleRepository.findById(tenantProgram.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found for id: " + tenantProgram.getRoleId()));

            String tenantRoleCode = tenantRole.getCode();

            // Check if referral code exists
            boolean tenantExists = referralCodeRepository.existsByProgramIdAndOwnerId(tenantProgram.getId(), tenant.getId());
            if (!tenantExists) {
                ReferralCode tenantReferral = new ReferralCode();
                tenantReferral.setProgramId(tenantProgram.getId());
                tenantReferral.setOwnerId(tenant.getId());
                tenantReferral.setOwnerType(tenantRoleCode);
                tenantReferral.setCode(ReferralCodeUtil.buildTenantReferralCode(tenant.getId(), user.getName()));
                tenantReferral.setStatus("ACTIVE");
                tenantReferral.setMaxUses(0);
                tenantReferral.setUsedCount(0);

                referralCodeRepository.save(tenantReferral);
             // Fetch the active TenantUserRole for the tenant
                Optional<TenantUserRole> optionalTur = tenantUserRoleRepository
                        .findFirstByTenantIdAndIsActiveTrue(tenant.getId());

                if (optionalTur.isEmpty()) {
                    throw new RuntimeException("No active user found for tenantId: " + tenant.getId());
                }

                TenantUserRole tenantUserRole = optionalTur.get();

                // Fetch the User entity using userId from TenantUserRole
                User tenantUser = userRepository.findById(tenantUserRole.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found for tenantId: " + tenant.getId()));

                //  Get the name safely
                String tenantUserName = tenantUser.getName() != null && !tenantUser.getName().isBlank()
                        ? tenantUser.getName()
                        : "User";

                // Send the email
                emailService.sendTenantReferralEmail(
                        tenant.getEmail(),      // Tenant email
                        tenantUserName,         // Name from User table
                        tenantReferral.getCode() // Generated referral code
                );


            }
            
            // Step 5: Send Email Verification
            String token = jwtUtil.generateEmailVerificationToken(user.getId(), user.getEmail());
            emailService.sendVerificationEmail(user.getEmail(), token);

        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateEntryException("Duplicate data detected");
        } catch (MailException ex) {
            throw new UserNotAllowedException("Verification email could not be sent");
        }
    }

    @Transactional
    public void registerUserOnly(TenantSignupRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateEntryException("Email already registered");
        }

        try {
            // Create only the User row and send verification
            User user = new User();
            user.setName(req.getName());
            user.setEmail(req.getEmail());
            user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
            user.setPhone(req.getPhone());
            user.setActive(true);
            user.setSuperadmin(false);
            userRepository.save(user);

            String token = jwtUtil.generateEmailVerificationToken(user.getId(), user.getEmail());
            emailService.sendVerificationEmail(user.getEmail(), token);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateEntryException("Duplicate data detected");
        } catch (MailException ex) {
            throw new UserNotAllowedException("Verification email could not be sent");
        }
    }

    @Transactional
    public void registerTenantForTrial(TenantTrialRequest req) {
        Tenant tenant = tenantRepository.findByEmail(req.email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tenant not found with email: " + req.email));

        User user = userRepository.findByEmail(req.email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with email: " + req.email));

        // Validate password
        if (!passwordEncoder.matches(req.password, user.getPasswordHash())) {
            throw new UserNotAllowedException("Invalid password");
        }

        if (tenant.getStatus().equals("trial")) {
            throw new UserNotAllowedException("Tenant is already on a trial period");
        } else if (tenant.getStatus().equals("active")) {
            throw new UserNotAllowedException("Active tenants cannot start a trial period");
        }

        Long tenantId = tenant.getId();
        System.out.println("[DEBUG] Extracted tenantId: " + tenantId);
        if (tenantId == null) {
            throw new UserNotAllowedException("Invalid token: Tenant ID not found");
        }
        try {
            TenantSetting tenantSetting = new TenantSetting();
            tenantSetting.setTenantId(tenantId);
            tenantSetting.setTimezone(req.timeZone);
            tenantSetting.setCurrency(req.currency);
            tenantSetting.setMaxCompanies(trialConfig.getCompaniesCount());
            tenantSetting.setTrialDays(trialConfig.getTrialDays());
            tenantSetting.setExtendedTrialDays(trialConfig.getExtendedTrialDays());
            tenantSetting.setAdsUnlockedEnabled(trialConfig.isAdsUnlockedEnabled());
            tenantSetting.setAdsUnlockedDays(trialConfig.getAdsUnlockedDays());
            tenantSetting.setCreatedAt(Instant.now());
            tenantSetting.setUpdatedAt(Instant.now());
            tenantSettingsRepository.save(tenantSetting);
        } catch (Exception e) {
            throw new UserNotAllowedException("Tenant settings could not be created: " + e.getMessage());
        }

        try {
            TenantUsage tenantUsage = new TenantUsage();
            tenantUsage.setTenantId(tenantId);
            tenantUsage.setActiveUsersCount(trialConfig.getActiveUsersCount());
            tenantUsage.setCompaniesCount(trialConfig.getCompaniesCount());
            tenantUsage.setCreatedAt(Instant.now());
            tenantUsage.setUpdatedAt(Instant.now());
            tenantUsageRepository.save(tenantUsage);
            TrialPeriod trialPeriod = getTrialPeriodDates(tenantId);
            tenantRepository.updateTenantStatusToTrial(
                    tenantId,
                    trialPeriod.getTrialStartAt(),
                    trialPeriod.getTrialEndAt());
        } catch (Exception e) {
            throw new UserNotAllowedException("Tenant usage could not be created: " + e.getMessage());
        }
    }

    public TrialPeriod getTrialPeriodDates(Long tenantId) {
        TenantSetting setting = tenantSettingsRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new UserNotFoundException("Tenant not found"));

        int extendedTrialDays = setting.getExtendedTrialDays();
        int trialDays = setting.getTrialDays();
        LocalDateTime trialStartAt = LocalDateTime.now();
        LocalDateTime trialEndAt = trialStartAt.plusDays(extendedTrialDays + trialDays);

        return new TrialPeriod(trialStartAt, trialEndAt);
    }
    
    
    @Transactional
    public void registerUserWithExistingTenant(TenantSignupRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateEntryException("Email already registered");
        }

        Tenant tenant = tenantRepository.findById(req.getExistingTenantId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Tenant not found"));

        // 🔐 Generate password at backend (inline)
        String tempPassword = new BigInteger(50, new SecureRandom()).toString(32);

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setPasswordHash(passwordEncoder.encode(tempPassword));
        user.setActive(true);
        user.setSuperadmin(false);
        userRepository.save(user);

        TenantUserRole tur = new TenantUserRole();
        tur.setTenantId(tenant.getId());
        tur.setUserId(user.getId());
        tur.setRoleId(3L); // TENANT_ADMIN / TENANT_USER
        tur.setIsActive(true);
        tenantUserRoleRepository.save(tur);

        // Email verification / invite
        String token = jwtUtil.generateEmailVerificationToken(
                user.getId(), user.getEmail());
        emailService.sendVerificationEmail(user.getEmail(), token);
    
    
    // 2️⃣ Send temporary password email
    emailService.sendTemporaryPasswordEmail(
            user,
            tempPassword
    );

    }
 
}

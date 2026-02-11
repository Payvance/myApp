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

import com.payvance.erp_saas.core.dto.LoginRequest;
import com.payvance.erp_saas.core.dto.LoginResponse;
import com.payvance.erp_saas.core.entity.*;
import com.payvance.erp_saas.core.enums.RoleEnum;
import com.payvance.erp_saas.core.repository.*;
import com.payvance.erp_saas.exceptions.*;
import com.payvance.erp_saas.core.dto.DeviceChangeVerifyRequest;
import com.payvance.erp_saas.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final TenantUserRoleRepository tenantUserRoleRepo;
    private final TenantRepository tenantRepo;
    private final VendorRepository vendorRepo;
    private final CaRepository caRepo;
    private final PersonalAccessTokenRepository patRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;
    private final EmailOtpService emailOtpService;

    // ========================== LOGIN ==========================
    @Transactional
    public LoginResponse login(LoginRequest req) {

        String email = req.email.trim().toLowerCase();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new UserNotAllowedException("Account disabled");
        }

        if (!encoder.matches(req.password, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // ================= DEVICE VERIFICATION =================
        if (req.platform != null
                && (req.platform.equalsIgnoreCase("android") || req.platform.equalsIgnoreCase("ios"))) {
            if (user.getMobileDeviceId() == null) {
                // First time login from mobile
                user.setMobileDeviceId(req.deviceId);
                user.setMobilePlatform(req.platform);
                user.setMobileFcmToken(req.fcmToken);
                user.setMobileDeviceModel(req.deviceModel);
                user.setMobileOsVersion(req.osVersion);
                user.setMobileAppVersion(req.appVersion);
                userRepo.save(user);
            } else if (!user.getMobileDeviceId().equals(req.deviceId)) {
                // throw new DeviceMismatchException(
                // "Login from new device detected (" + req.deviceModel + "). Verify to switch
                // device.");
            } else {
                // Same device, update details
                user.setMobileFcmToken(req.fcmToken);
                user.setMobileDeviceModel(req.deviceModel);
                user.setMobileOsVersion(req.osVersion);
                user.setMobileAppVersion(req.appVersion);
                userRepo.save(user);
            }
        }

        // ================= DESKTOP CONNECTOR VERIFICATION =================
        if (req.platform != null && req.platform.equalsIgnoreCase("DESKTOP_CONNECTOR")) {

            if (req.deviceId == null || req.deviceId.isBlank()) {
                throw new BadCredentialsException("Device ID is missing for Desktop Connector.");
            }

            // 1. Check if ANY user is already bound to this device
            Optional<User> existingUserOpt = userRepo.findByDesktopDeviceId(req.deviceId);
            if (existingUserOpt.isPresent()) {
                User existingUser = existingUserOpt.get();

                if (!existingUser.getId().equals(user.getId())) {
                    // Device is used by SOMEONE ELSE -> Block
                    // throw new DeviceMismatchException(
                    // "This device is already linked to another user (" + existingUser.getEmail() +
                    // "). Please ask them to logout first.");
                }
            }

            // 2. Check if THIS user is bound to a DIFFERENT device
            if (user.getDesktopDeviceId() == null) {
                // First time login from desktop -> Bind to this device
                user.setDesktopDeviceId(req.deviceId);
                userRepo.save(user);

            } else if (!user.getDesktopDeviceId().equals(req.deviceId)) {
                // Different device -> Block
                // throw new DeviceMismatchException(
                // "This account is linked to another Desktop. Please use the registered
                // device.");
            }
        }

        if (user.getEmailVerifiedAt() == null) {
            throw new UserNotAllowedException("Go and verify your email");
        }
        // for currently, we are allowing multiple sessions as we are moving towards
        // channel-based login
        // if (patRepo.existsByUserId(user.getId())) {
        // throw new UserNotAllowedException("User already logged in elsewhere");
        // }

        return processUserLogin(user);
    }

    private LoginResponse processUserLogin(User user) {

        // ================= SUPER ADMIN =================
        if (user.isSuperadmin()) {

            RoleEnum roleEnum = RoleEnum.SUPER_ADMIN;
            Long roleId = Long.valueOf(roleEnum.getId());

            String token = jwt.generateAccessToken(user.getId(), null, roleId);
            savePAT(user.getId(), null, roleEnum, token);

            return LoginResponse.builder()
                    .accessToken(token)
                    .roleId(roleId)
                    .userId(user.getId()) // for frontend use
                    .redirectUrl("/superadmin/dashboard")
                    .message("Login successful")
                    .build();
        }

        // ================= TENANT ROLE =================
        Optional<TenantUserRole> turOpt = tenantUserRoleRepo
                .findFirstByUserIdAndIsActiveTrueOrderByRoleIdAsc(user.getId());

        if (turOpt.isPresent()) {
            TenantUserRole tur = turOpt.get();
            RoleEnum roleEnum = RoleEnum.fromId(tur.getRoleId().intValue());
            return loginRedirect(user, roleEnum, tur.getTenantId());
        }

        // ================= PARTNER =================
        var partnerResult = vendorRepo.findByUserId(user.getId())
                .map(v -> partnerRedirect(user, RoleEnum.VENDOR, v))
                .orElseGet(() -> caRepo.findByUserId(user.getId())
                        .map(c -> partnerRedirect(user, RoleEnum.CA, c))
                        .orElse(null));

        if (partnerResult != null) {
            return partnerResult;
        }

        // If user has no role mapping (signup without role), redirect frontend to
        // profile/role selection page so they can complete signup. No access token
        // is issued here; frontend should navigate to the provided URL and collect
        // role/profile details before calling the upsert endpoints.
        return LoginResponse.builder()
                .userId(user.getId())
                .accessToken(null)
                .roleId(null)
                .redirectUrl("/profile-form")
                .message("Complete your profile to continue")
                .build();
    }

    // ================= TENANT REDIRECT =================
    private LoginResponse loginRedirect(User user, RoleEnum roleEnum, Long tenantId) {

        Tenant tenant = tenantRepo.findById(tenantId)
                .orElseThrow(() -> new UserNotAllowedException("Tenant not found"));

        Long roleId = Long.valueOf(roleEnum.getId());

        // active / trial → normal login
        if (tenant.isActiveOrTrial()) {

            String token = jwt.generateAccessToken(user.getId(), tenantId, roleId);
            savePAT(user.getId(), tenantId, roleEnum, token);

            String redirect = roleEnum == RoleEnum.TENANT_ADMIN
                    ? "/tenant/admin/dashboard"
                    : "/tenant/user/dashboard";

            return LoginResponse.builder()
                    .accessToken(token)
                    .roleId(roleId)
                    .userId(user.getId()) // for frontend use
                    .tenantId(tenantId)
                    .redirectUrl(redirect)
                    .message("Login successful")
                    .build();
        }

        // inactive → allow login but redirect to plan page
        if (tenant.isInactive()) {

            String token = jwt.generateAccessToken(user.getId(), tenantId, roleId);
            savePAT(user.getId(), tenantId, roleEnum, token);

            return LoginResponse.builder()
                    .accessToken(token)
                    .roleId(roleId)
                    .userId(user.getId()) // for frontend use
                    .tenantId(tenantId)
                    .redirectUrl("/tenant/plan")
                    .message("Tenant plan inactive")
                    .build();
        }

        throw new UserNotAllowedException(
                "Tenant not eligible to login: " + tenant.getStatus());
    }

    // ================= PARTNER REDIRECT =================
    private LoginResponse partnerRedirect(User user, RoleEnum roleEnum, Object partner) {

        String status = partner instanceof Vendor v ? v.getStatus() : ((Ca) partner).getStatus();

        if ("disabled".equalsIgnoreCase(status)) {
            throw new UserNotAllowedException("Account disabled. Contact Payvance");
        }

        Long roleId = Long.valueOf(roleEnum.getId());
        String token = jwt.generateAccessToken(user.getId(), null, roleId);
        savePAT(user.getId(), null, roleEnum, token);

        String redirect = switch (status) {
            case "draft" -> "/" + roleEnum.name().toLowerCase() + "/profile-form";
            case "pending_approval" -> "/" + roleEnum.name().toLowerCase() + "/waiting-approval";
            case "approved" -> "/" + roleEnum.name().toLowerCase() + "/dashboard";
            case "rejected" -> "/" + roleEnum.name().toLowerCase() + "/rejected";
            default -> throw new UserNotAllowedException("Invalid status");
        };

        return LoginResponse.builder()
                .accessToken(token)
                .roleId(roleId)
                .userId(user.getId()) // for frontend use
                .redirectUrl(redirect)
                .message("Login successful")
                .build();
    }

    // ================= SAVE TOKEN =================
    private void savePAT(Long userId, Long tenantId, RoleEnum roleEnum, String token) {

        patRepo.save(PersonalAccessToken.builder()
                .tokenId(jwt.getTokenId(token))
                .userId(userId)
                .tenantId(tenantId)
                .tokenableId(userId)
                .tokenableType(roleEnum.name())
                .name("ACCESS_TOKEN")
                .expiresAt(jwt.getExpiration(token))
                .build());
    }

    // ================= LOGOUT =================
    @Transactional
    public void logout(String token) {
        patRepo.deleteByTokenId(jwt.getTokenId(token));
    }

    // ================= EMAIL VERIFY =================
    @Transactional
    public void verifyEmail(String token) {

        if (!jwt.validateToken(token) || !jwt.isEmailVerificationToken(token)) {
            throw new BadCredentialsException("Invalid or expired token");
        }

        User user = userRepo.findById(jwt.getUserId(token))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setEmailVerifiedAt(LocalDateTime.now());
        userRepo.save(user);
    }

    // ================= VERIFY CURRENT PASSWORD =================
    public void verifyCurrentPassword(String token, String currentPassword) {

        if (!jwt.validateToken(token)) {
            throw new BadCredentialsException("Invalid token");
        }

        Long userId = jwt.getUserId(token);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!encoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("Current password is incorrect");
        }
    }

    // ================= SET NEW PASSWORD =================
    @Transactional
    public void setNewPassword(String token, String newPassword) {

        if (!jwt.validateToken(token)) {
            throw new BadCredentialsException("Invalid token");
        }

        Long userId = jwt.getUserId(token);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPasswordHash(encoder.encode(newPassword));
        userRepo.save(user);

    }

    // ================= DEVICE CHANGE =================
    public void initiateDeviceChange(LoginRequest req) {
        String email = req.email.trim().toLowerCase();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!encoder.matches(req.password, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid password");
        }

        emailOtpService.sendOtp(email);
    }

    public LoginResponse verifyDeviceChange(DeviceChangeVerifyRequest req) {
        emailOtpService.verifyOtp(req.email, req.otp);

        String email = req.email.trim().toLowerCase();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setMobileDeviceId(req.deviceId);
        user.setMobilePlatform(req.platform);
        user.setMobileFcmToken(req.fcmToken);
        user.setMobileDeviceModel(req.deviceModel);
        user.setMobileOsVersion(req.osVersion);
        user.setMobileAppVersion(req.appVersion);
        userRepo.save(user);

        return processUserLogin(user);
    }

}

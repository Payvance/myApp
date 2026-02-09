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

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.payvance.erp_saas.core.entity.Ca;
import com.payvance.erp_saas.core.entity.ReferralCode;
import com.payvance.erp_saas.core.entity.User;
import com.payvance.erp_saas.core.entity.Vendor;
import com.payvance.erp_saas.core.notification.email.EmailJob;
import com.payvance.erp_saas.core.notification.email.EmailTemplateService;
import com.payvance.erp_saas.core.repository.CaRepository;
import com.payvance.erp_saas.core.repository.ReferralCodeRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
import com.payvance.erp_saas.core.repository.VendorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailTemplateService templateService;
    private final EmailJob emailJob;
    private final UserRepository userRepository;
    private final CaRepository caRepository;
    private final VendorRepository vendorRepository;
    private final ReferralCodeRepository referralCodeRepository;

    @Value("${app.frontend.verify-url}")
    private String verifyUrl;

    public void sendVerificationEmail(String to, String token) {

        if (to == null || token == null) {
            throw new IllegalArgumentException("Email or token cannot be null");
        }

        String link = verifyUrl + "/api/auth/verify-email?token=" + token;

        Map<String, Object> variables = Map.of(
                "verifyLink", link);

        String html = templateService.process(
                "email/verification-email",
                variables);

        emailJob.sendHtmlEmail(
                to,
                "Verify your email",
                html, null);
    }

    public void sendTrialStartedEmail(String to, java.time.LocalDateTime start, java.time.LocalDateTime end) {

        if (to == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        
        Map<String, Object> variables = Map.of(
        		"trial_start_at", start.format(formatter),
                "trial_end_at", end.format(formatter));

        String html = templateService.process(
                "email/trial-started-email",
                variables);

        // Use synchronous send for critical trial-started email so failures are visible
        // and can cause transaction rollback if desired. Async sends are fire-and-forget
        // and exceptions are handled by the async exception handler (not propagated).
        emailJob.sendHtmlEmailSync(
                to,
                "Your trial has started",
                html, null);
    }
    
    
    
 // ================= PROFILE SUBMITTED =================
    public void sendProfileSubmittedEmail(Long userId) {
        // Fetch User details from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        String to = user.getEmail();
        String name = (user.getName() != null && !user.getName().isBlank()) 
                      ? user.getName() : "Valued Customer";

        Map<String, Object> variables = Map.of(
                "name", name
        );

        String html = templateService.process(
                "email/profile-submitted",
                variables
        );

        emailJob.sendHtmlEmail(
                to,
                "Profile Submitted – Awaiting Verification",
                html, null
        );
    }
    
    
 // ================= PROFILE APPROVAL / REJECTION =================
    public void sendProfileApprovalRejectionEmail(
            Long userId,
            boolean approved,
            String remark,
            String referralCode
            
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found with ID: " + userId));

        String to = user.getEmail();
        String name = (user.getName() != null && !user.getName().isBlank())
                ? user.getName()
                : "User";

        String finalRemark = remark;
        
        String referralCodes = referralCode;
        if (approved) {

            Ca ca = caRepository
                    .findByUserIdAndStatus(userId, "APPROVED")
                    .orElse(null);

            if (ca != null) {
                referralCodes = referralCodeRepository
                        .findByOwnerTypeAndOwnerIdAndStatus(
                                "CA",
                                ca.getId(),
                                "active"
                        )
                        .map(ReferralCode::getCode)
                        .orElse("");
            }
        }

        // 🔥 FETCH REJECTION REMARK FROM CA / VENDOR TABLE IF REJECTED
        if (!approved && (finalRemark == null || finalRemark.isBlank())) {

            Vendor vendor = vendorRepository
                    .findByUserIdAndStatus(userId, "REJECTED")
                    .orElse(null);

            Ca ca = caRepository
                    .findByUserIdAndStatus(userId, "REJECTED")
                    .orElse(null);

            if (vendor != null) {
                finalRemark = vendor.getRejectionRemark();
            } else if (ca != null) {
                finalRemark = ca.getRejectionRemark();
            } else {
                finalRemark = "Your profile was rejected due to policy reasons.";
            }
        }

        String status = approved ? "approved" : "rejected";

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("status", status);
        variables.put("remark", approved ? "" : finalRemark);
        variables.put("referralCode", referralCodes);


        String html = templateService.process(
                "email/approval-rejection",
                variables
        );

        String subject = approved
                ? "Profile Approved Successfully 🎉"
                : "Profile Rejected ❌";

        emailJob.sendHtmlEmail(
                to,
                subject,
                html, null
        );
    }

    private void sendUniversalEmail(
            String to,
            String subject,
            Map<String, Object> variables,
            List<String> ccEmails
    ) {
        String html = templateService.process(
                "email/universal-email",
                variables
        );

        emailJob.sendHtmlEmail(
                to,
                subject,
                html,
                ccEmails
        );
    }
    public void sendTemporaryPasswordEmail(User user, String tempPassword) {

        Map<String, Object> vars = new HashMap<>();
        vars.put("subject", "Your Temporary Password");
        vars.put("title", "Account Access Details");
        vars.put("name", user.getName());
        vars.put("message",
                "Your account has been created. Please use the temporary password below to login.");
        vars.put("password", tempPassword);
        vars.put("actionUrl", verifyUrl + "/login");
        vars.put("actionText", "Login");
        vars.put("status", "approved");

        sendUniversalEmail(
                user.getEmail(),
                "Your Temporary Password",
                vars, null
        );
    }

    
    public void sendLicenseIssuedEmail(String toEmail, List<String> ccVendorEmail, String plainLicenseKey) {
        if (toEmail == null || plainLicenseKey == null) {
            throw new IllegalArgumentException("Email and license key cannot be null");
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("subject", "Your License Key is Generated");
        variables.put("title", "License Key Details");
        variables.put("name", toEmail); // if user object is not available, use email as name
        variables.put("message", "A new license key has been generated for your account.");
        variables.put("info", "License Key: " + plainLicenseKey );
        variables.put("actionUrl", verifyUrl + "/login"); // redirect to login or license dashboard
        variables.put("actionText", "View License");
        variables.put("status", "approved");

        sendUniversalEmail(toEmail, "Your License Key is Generated", variables, ccVendorEmail);
    }

 // ================= TENANT REFERRAL EMAIL =================
    public void sendTenantReferralEmail(String toEmail, String tenantName, String referralCode) {

        if (toEmail == null || referralCode == null) {
            throw new IllegalArgumentException("Email or referral code cannot be null");
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("subject", "Welcome! Your Tenant Referral Code");
        variables.put("title", "Tenant Referral Code Created");
        variables.put("name", tenantName != null && !tenantName.isBlank()
                ? tenantName
                : "User");
        variables.put("message", "Your referral code has been successfully generated for your organization.");
        variables.put("info", "Referral Code: " + referralCode);
        variables.put("actionUrl", verifyUrl + "/login");
        variables.put("actionText", "Go to Dashboard");
        variables.put("status", "approved");

        sendUniversalEmail(
                toEmail,
                "Your Tenant Referral Code 🎉",
                variables,
                null   // ✅ no CC
        );
    }
    
 // ================= OTP EMAIL =================
    public void sendOtpEmail(String toEmail, String otp) {

        if (toEmail == null || otp == null) {
            throw new IllegalArgumentException("Email or OTP cannot be null");
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("subject", "Your One-Time Password (OTP)");
        variables.put("title", "OTP Verification");
        variables.put("name", toEmail); // no user object, fallback to email
        variables.put("otp", otp);
       
        sendUniversalEmail(
            toEmail,
            "Your OTP Code",
            variables,
            null // no CC
        );
    }

// ================= SUPPORT UNIVERSAL EMAIL FOR REDMINE ==================

public void SupportSendUniversalEmail(
        String to,
        String subject,
        Map<String, Object> variables,
        List<String> ccEmails
) {
    String html = templateService.process(
            "email/universal-email",
            variables
    );

    emailJob.sendHtmlEmail(
            to,
            subject,
            html,
            ccEmails
    );
}


}

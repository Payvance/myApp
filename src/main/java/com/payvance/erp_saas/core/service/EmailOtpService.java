package com.payvance.erp_saas.core.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.payvance.erp_saas.core.entity.EmailOtp;
import com.payvance.erp_saas.core.entity.User;
import com.payvance.erp_saas.core.repository.EmailOtpRepository;
import com.payvance.erp_saas.core.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailOtpService {

	private final EmailOtpRepository emailOtpRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Value("${app.mail.otp-expiry-minutes}")
    private int otpExpiryMinutes;

    @Transactional
    public void sendOtp(String email) {

        // 1. Check user exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not registered with this email"));

        // 2. Check existing OTP
        Optional<EmailOtp> existingOtpOpt = emailOtpRepository.findByEmail(email);

        if (existingOtpOpt.isPresent()) {
            EmailOtp existingOtp = existingOtpOpt.get();

            if (existingOtp.getExpiresAt().isAfter(LocalDateTime.now())) {
                throw new RuntimeException("An OTP has already been sent. Please check your email.");
            } else {
                emailOtpRepository.delete(existingOtp);
                emailOtpRepository.flush();
            }
        }

        // 3. Generate OTP
        SecureRandom random = new SecureRandom();
        String otp = String.valueOf(100000 + random.nextInt(900000));

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(email);
        emailOtp.setOtp(otp);
        emailOtp.setCreatedAt(LocalDateTime.now());
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));

        emailOtpRepository.save(emailOtp);
        emailOtpRepository.flush();

        //  4. Send email
        emailService.sendOtpEmail(email, otp);
    }


    @Transactional
    public void verifyOtp(String email, String otp) {

        EmailOtp emailOtp = emailOtpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        // 🔥 FIX 3: Expiry check + cleanup
        if (emailOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            emailOtpRepository.delete(emailOtp);
            throw new RuntimeException("OTP expired");
        }

        // 🔥 FIX 4: Match OTP
        if (!emailOtp.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // 🔥 FIX 5: OTP USED → DELETE
        emailOtpRepository.delete(emailOtp);
    }
}

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
 * Anjor         	 1.0.0       30-Dec-2025    class created
 *
 **/
package com.payvance.erp_saas.core.controller;

import java.security.SecureRandom;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.payvance.erp_saas.core.service.SmsService;
import com.payvance.erp_saas.exceptions.UserNotAllowedException;
import com.payvance.erp_saas.core.notification.sms.OtpStore;

@RestController
@RequestMapping("/api/otp")
public class SmsOtpController {

    private final SmsService smsService;
    private final OtpStore otpStore;
    private final SecureRandom random = new SecureRandom();

    public SmsOtpController(SmsService smsService, OtpStore otpStore) {
        this.smsService = smsService;
        this.otpStore = otpStore;
    }

    @PostMapping("/send")
    public String sendOtp(@RequestParam String mobile) {

        String otp = String.valueOf(100000 + random.nextInt(900000));

        otpStore.save(mobile, otp, 5);

        smsService.sendSms(
                "otp",
                mobile,
                Map.of("otp", otp));

        return "OTP sent";
    }

    @PostMapping("/verify")
    public String verifyOtp(
            @RequestParam String mobile,
            @RequestParam String otp) {

        boolean valid = otpStore.verify(mobile, otp);

        if (!valid) {
            throw new UserNotAllowedException("Invalid or expired OTP");
        }

        return "OTP verified successfully";
    }
}

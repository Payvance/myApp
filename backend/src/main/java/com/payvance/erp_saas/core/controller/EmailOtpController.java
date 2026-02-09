package com.payvance.erp_saas.core.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.dto.OtpRequest;
import com.payvance.erp_saas.core.service.EmailOtpService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/email/otp")
@RequiredArgsConstructor
public class EmailOtpController {
	
	private final EmailOtpService emailOtpService;

	
	//Send otp on email
	@PostMapping("/send")
	public ResponseEntity<?> sendOtp(@RequestBody OtpRequest request) {
	    try {
	        emailOtpService.sendOtp(request.getEmail());

	        return ResponseEntity.ok(
	                Map.of(
	                        "success", true,
	                        "message", "OTP sent successfully",
	                        "email", request.getEmail()
	                )
	        );
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(Map.of(
	                        "success", false,
	                        "message", e.getMessage()
	                ));
	    }
    }

    //  VERIFY OTP
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest request) {

        emailOtpService.verifyOtp(request.getEmail(), request.getOtp());

        return ResponseEntity.ok(
                Map.of(
                        "message", "OTP verified successfully"
                )
        );
    }

}

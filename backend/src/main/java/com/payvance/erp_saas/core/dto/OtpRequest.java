package com.payvance.erp_saas.core.dto;

import lombok.Data;

@Data
public class OtpRequest {
	 private String email;
	    private String otp; // optional for sending, required for verify

}

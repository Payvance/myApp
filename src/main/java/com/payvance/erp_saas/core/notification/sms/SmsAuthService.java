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
package com.payvance.erp_saas.core.notification.sms;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.payvance.erp_saas.core.config.SmsConfig;

@Service
public class SmsAuthService {
    private final SmsConfig smsConfig;
    // private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;

    public SmsAuthService(SmsConfig smsConfig) {
        this.smsConfig = smsConfig;
        this.restTemplate = new RestTemplate();
    }

    public String refreshToken() {
    	String username = smsConfig.getUsername().trim().replaceAll("\\s+", "");
    	String password = smsConfig.getPassword().trim().replaceAll("\\s+", "");


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(
                smsConfig.getBearerToken());
        String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(smsConfig.getLoginUrl(), HttpMethod.POST, request,
                String.class);

        String token = extractToken(response.getBody());
        // restTemplate.opsForValue().set("sms_token", token, 6, TimeUnit.HOURS);
        return token;
    }

    private String extractToken(String responseBody) {
        return responseBody.split("\"accessToken\":\"")[1].split("\"")[0];
    }
}

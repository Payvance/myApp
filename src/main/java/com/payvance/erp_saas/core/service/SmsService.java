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
package com.payvance.erp_saas.core.service;

import java.util.HashMap;
import java.util.Map;

import com.payvance.erp_saas.core.config.SmsConfig;
import com.payvance.erp_saas.core.notification.sms.SmsAuthService;
import com.payvance.erp_saas.core.notification.sms.SmsTemplateService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SmsService {

    private final SmsAuthService smsAuthService;
    private final SmsTemplateService templateService;
    private final RestTemplate restTemplate;
    private final SmsConfig smsConfig;

    public SmsService(SmsAuthService smsAuthService, SmsTemplateService templateService,
            SmsConfig smsConfig) {
        this.smsAuthService = smsAuthService;
        this.templateService = templateService;
        this.restTemplate = new RestTemplate();
        this.smsConfig = smsConfig;
    }

    public String sendSms(String templateName, String mobile, Map<String, String> variables) {
        String message = templateService.processTemplate(templateName, variables);

        // Get fixed configuration values from the YML
        String sender = smsConfig.getSender().trim().replaceAll("\\s+", "");
        String messageType = smsConfig.getMessageType().trim().replaceAll("\\s+", "");
        String peId = smsConfig.getPeId().trim().replaceAll("\\s+", "");
        String tempId = smsConfig.getTempId().trim().replaceAll("\\s+", "");
        String smsApiUrl = smsConfig.getApiUrl().trim().replaceAll("\\s+", "");

        // Prepare a map of all placeholders for the template
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("sender", sender);
        placeholders.put("mobileNo", mobile);
        placeholders.put("messageType", messageType);
        placeholders.put("message", message);
        placeholders.put("peId", peId);
        placeholders.put("tempId", tempId);

        // Get the request template from configuration
        String requestTemplate = smsConfig.getRequestTemplate();

        // Replace placeholders in the template (simple implementation)
        String requestBody = replacePlaceholders(requestTemplate, placeholders);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = smsAuthService.refreshToken();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(smsApiUrl, HttpMethod.POST, request, String.class);
        // return response.getBody();

        String responseBody = response.getBody();

        // Use Jackson to parse the JSON response
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            if (root.has("message") && "Message Sent Successfully".equals(root.get("message").asText())) {
                return "Success";
            }
        } catch (Exception e) {
            // Log error if needed, but fallback to returning the original response
        }
        return responseBody;
    }

    // Simple placeholder replacement utility
    private String replacePlaceholders(String template, Map<String, String> placeholders) {
        String result = template;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}

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

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.payvance.erp_saas.core.config.SmsConfig;

@Component
public class SmsRequestBuilder {

    private final SmsConfig smsConfig;

    public SmsRequestBuilder(SmsConfig smsConfig) {
        this.smsConfig = smsConfig;
    }

    public String build(String mobile, String message, String templateName) {

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("sender", smsConfig.getSender());
        placeholders.put("mobileNo", mobile);
        placeholders.put("messageType", smsConfig.getMessageType());
        placeholders.put("message", message);
        placeholders.put("peId", smsConfig.getPeId());
        placeholders.put("tempId", templateName);

        String template = smsConfig.getRequestTemplate();

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace(
                    "${" + entry.getKey() + "}",
                    entry.getValue());
        }
        return template;
    }
}

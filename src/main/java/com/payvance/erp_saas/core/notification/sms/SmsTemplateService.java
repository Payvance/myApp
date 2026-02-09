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

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
public class SmsTemplateService {

    public String processTemplate(String templateName, Map<String, String> variables) {
        String content = loadTemplate(templateName);

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            content = content.replace(
                    "{" + entry.getKey() + "}",
                    entry.getValue());
        }
        return content;
    }

    private String loadTemplate(String templateName) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/sms/" + templateName + ".txt");
            InputStream inputStream = resource.getInputStream();
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("SMS template not found: " + templateName);
        }
    }
}

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
package com.payvance.erp_saas.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.sms")
public class SmsConfig {

    // ===== SMS Send API =====
    private String apiUrl;
    private String sender;
    private String messageType;
    private String peId;
    private String requestTemplate;
    private String tempId;
    private String bearerToken;

    // ===== SMS Auth =====
    private String loginUrl;
    private String username;
    private String password;
}

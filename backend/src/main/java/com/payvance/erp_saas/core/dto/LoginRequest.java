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
 * Anjor         	 1.0.0       26-Dec-2025    class created
 *
 **/
package com.payvance.erp_saas.core.dto;

public class LoginRequest {
    public String email;
    public String password;

    // Device Information
    public String deviceId;
    public String fcmToken;
    public String platform; // "android" | "ios" | "web"
    public String deviceModel; // optional
    public String osVersion; // optional
    public String appVersion; // optional
}

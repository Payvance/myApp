package com.payvance.erp_saas.core.dto;

import lombok.Data;

@Data
public class DeviceChangeVerifyRequest {
    public String email;
    public String otp;

    // New device details to register
    public String deviceId;
    public String fcmToken;
    public String platform;
    public String deviceModel;
    public String osVersion;
    public String appVersion;
}

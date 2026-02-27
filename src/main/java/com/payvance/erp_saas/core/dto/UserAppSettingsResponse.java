package com.payvance.erp_saas.core.dto;

import com.payvance.erp_saas.core.enums.NotificationChannel;
import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class UserAppSettingsResponse {

    private Long userId;

    private boolean pushEnabled;
    private boolean promosEnabled;

    private Set<NotificationChannel> notificationChannels;

    private boolean locationEnabled;
    private boolean mobileDataEnabled;
    private boolean contactsEnabled;
}
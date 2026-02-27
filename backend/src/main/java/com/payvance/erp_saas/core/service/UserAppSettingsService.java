package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.dto.UserAppSettingsRequest;
import com.payvance.erp_saas.core.dto.UserAppSettingsResponse;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.payvance.erp_saas.core.entity.UserAppSettings;
import com.payvance.erp_saas.core.repository.UserAppSettingsRepository;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserAppSettingsService {

    private final UserAppSettingsRepository repository;

    public UserAppSettingsResponse getSettings(Long userId) {

        UserAppSettings settings = repository.findById(userId)
                .orElseGet(() -> createDefault(userId));

        return mapToResponse(settings);
    }

    public UserAppSettingsResponse saveOrUpdate(
            Long userId,
            UserAppSettingsRequest request) {

        UserAppSettings settings = repository.findById(userId)
                .orElse(UserAppSettings.builder()
                        .userId(userId)
                        .build());

        settings.setPushEnabled(request.isPushEnabled());
        settings.setPromosEnabled(request.isPromosEnabled());
        settings.setNotificationChannels(
                request.getNotificationChannels() != null
                        ? request.getNotificationChannels()
                        : new HashSet<>()
        );
        settings.setLocationEnabled(request.isLocationEnabled());
        settings.setMobileDataEnabled(request.isMobileDataEnabled());
        settings.setContactsEnabled(request.isContactsEnabled());

        repository.save(settings);

        return mapToResponse(settings);
    }

    private UserAppSettings createDefault(Long userId) {

        UserAppSettings settings = UserAppSettings.builder()
                .userId(userId)
                .notificationChannels(new HashSet<>())
                .build();

        return repository.save(settings);
    }

    private UserAppSettingsResponse mapToResponse(UserAppSettings settings) {
        return UserAppSettingsResponse.builder()
                .userId(settings.getUserId())
                .pushEnabled(settings.isPushEnabled())
                .promosEnabled(settings.isPromosEnabled())
                .notificationChannels(settings.getNotificationChannels())
                .locationEnabled(settings.isLocationEnabled())
                .mobileDataEnabled(settings.isMobileDataEnabled())
                .contactsEnabled(settings.isContactsEnabled())
                .build();
    }
}
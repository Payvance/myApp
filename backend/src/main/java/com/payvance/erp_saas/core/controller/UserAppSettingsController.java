package com.payvance.erp_saas.core.controller;

import com.payvance.erp_saas.core.dto.UserAppSettingsRequest;
import com.payvance.erp_saas.core.dto.UserAppSettingsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.payvance.erp_saas.core.service.UserAppSettingsService;

@RestController
@RequestMapping("/api/users/{userId}/settings")
@RequiredArgsConstructor
public class UserAppSettingsController {

    private final UserAppSettingsService service;

    @GetMapping
    public ResponseEntity<UserAppSettingsResponse> getSettings(
            @PathVariable Long userId) {

        return ResponseEntity.ok(service.getSettings(userId));
    }

    @PutMapping
    public ResponseEntity<UserAppSettingsResponse> updateSettings(
            @PathVariable Long userId,
            @RequestBody UserAppSettingsRequest request) {

        return ResponseEntity.ok(
                service.saveOrUpdate(userId, request)
        );
    }
}
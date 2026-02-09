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
 * Anjor         	 1.0.0       28-Dec-2025    class created
 *
 **/
package com.payvance.erp_saas.core.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.payvance.erp_saas.core.dto.LoginRequest;
import com.payvance.erp_saas.core.dto.LoginResponse;
import com.payvance.erp_saas.core.dto.NewPassword;
import com.payvance.erp_saas.core.dto.Password;
import com.payvance.erp_saas.core.service.AuthService;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${app.frontend.redirect-url}")
    private String redirectUrl;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String auth) {
        String token = auth.substring(7);
        authService.logout(token);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity
                .status(HttpStatus.FOUND) // 302
                .location(URI.create(redirectUrl))
                .build();
    }
    @PostMapping("/verify-password")
    public ResponseEntity<Integer> verifyPassword(
            @RequestHeader("Authorization") String auth,
            @RequestBody Password req) {

        try {
            String token = auth.substring(7);
            authService.verifyCurrentPassword(token, req.getCurrentPassword());
            return ResponseEntity.ok(1);   
        } catch (Exception e) {
            return ResponseEntity.ok(0);  
        }
    }

    @PostMapping("/set-password")
    public ResponseEntity<?> setPassword(
            @RequestHeader("Authorization") String auth,
            @RequestBody NewPassword req) {

        String token = auth.substring(7);
        authService.setNewPassword(token, req.getNewPassword());

        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

}

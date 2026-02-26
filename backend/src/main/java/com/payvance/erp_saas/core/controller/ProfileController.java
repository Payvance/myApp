package com.payvance.erp_saas.core.controller;

import com.payvance.erp_saas.core.dto.LoginResponse;
import com.payvance.erp_saas.core.dto.ProfileRequest;
import com.payvance.erp_saas.core.dto.ProfileResponse;
import com.payvance.erp_saas.core.service.ProfileFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileFacadeService profileFacadeService;

    /**
     * Upsert profile based on ROLE_ID header
     *
     * @param request ProfileRequest JSON body (now contains roleId)
     * @return ProfileResponse
     */
    @PostMapping("/upsert")
    public ResponseEntity<LoginResponse> upsertProfile(
            @RequestBody ProfileRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Integer roleId = request.getRoleId();
        if (roleId == null) {
            return ResponseEntity.badRequest().build();
        }

        // Extract JWT from 
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;

        LoginResponse response =
                profileFacadeService.upsertProfile(roleId, request, token);

        return ResponseEntity.ok(response);
    }

    /**
     * FETCH profiles based on roleId provided in payload. userId is OPTIONAL.
     *
     * Note: we switched to POST here because the roleId is provided in the request body.
     */
    @PostMapping("/fetch")
    public ResponseEntity<List<ProfileResponse>> fetchProfiles(@RequestBody ProfileRequest request) {
        Integer roleId = request.getRoleId();
        Long userId = request.getUserId();

        if (roleId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<ProfileResponse> response = profileFacadeService.fetchProfiles(roleId, userId);
        return ResponseEntity.ok(response);
    }
}

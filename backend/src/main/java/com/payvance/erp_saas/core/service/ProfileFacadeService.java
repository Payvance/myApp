package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.dto.*;
import com.payvance.erp_saas.core.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
// Facade service to handle profile upsert based on user role
@Service
@RequiredArgsConstructor
public class ProfileFacadeService {

    private final VendorProfileService vendorProfileService;
    private final CaProfileService caProfileService;

    private final VendorProfileFetchService vendorProfileFetchService;
    private final CaProfileFetchService caProfileFetchService;

    /**
     * Upsert profile based on role
     */
    public ProfileResponse upsertProfile(Integer roleId, ProfileRequest request) {
        RoleEnum role = RoleEnum.fromId(roleId);

        switch (role) {
            case VENDOR:
                VendorRequest vendorRequest = request.toVendorRequest();
                return vendorProfileService.upsertVendorProfile(vendorRequest);
            case CA:
                CaRequest caRequest = request.toCaRequest();
                return caProfileService.upsertCaProfile(caRequest);
            default:
                throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }

     /**
     * FETCH profiles based on role
     * userId is OPTIONAL
     */
    public List<ProfileResponse> fetchProfiles(Integer roleId, Long userId) {
        RoleEnum role = RoleEnum.fromId(roleId);

        switch (role) {
            case VENDOR:
                return vendorProfileFetchService.fetchVendorProfiles(userId);

            case CA:
                return caProfileFetchService.fetchCaProfiles(userId);

            default:
                throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }
}

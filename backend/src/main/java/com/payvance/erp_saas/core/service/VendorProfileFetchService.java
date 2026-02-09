package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.dto.ProfileResponse;
import com.payvance.erp_saas.core.entity.Vendor;
import com.payvance.erp_saas.core.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
//  Service for fetching vendor profiles based on optional userId
@Service
@RequiredArgsConstructor
public class VendorProfileFetchService {

    private final VendorRepository vendorRepository;

    public List<ProfileResponse> fetchVendorProfiles(Long userId) {

        if (userId != null) {
            Vendor vendor = vendorRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

            return List.of(mapToResponse(vendor));
        }

        return vendorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProfileResponse mapToResponse(Vendor vendor) {
        return ProfileResponse.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .email(vendor.getEmail())
                .phone(vendor.getPhone())
                .status(vendor.getStatus())
                .build();
    }
}

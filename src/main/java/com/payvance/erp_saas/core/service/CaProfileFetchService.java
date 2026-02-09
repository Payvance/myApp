package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.dto.ProfileResponse;
import com.payvance.erp_saas.core.entity.Ca;
import com.payvance.erp_saas.core.repository.CaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
// Service for fetching CA profiles based on optional userId
@Service
@RequiredArgsConstructor
public class CaProfileFetchService {

    private final CaRepository caRepository;

    public List<ProfileResponse> fetchCaProfiles(Long userId) {

        if (userId != null) {
            Ca ca = caRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("CA not found"));

            return List.of(mapToResponse(ca));
        }

        return caRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProfileResponse mapToResponse(Ca ca) {
        return ProfileResponse.builder()
                .id(ca.getId())
                .name(ca.getName())
                .email(ca.getEmail())
                .phone(ca.getPhone())
                .status(ca.getStatus())
                .build();
    }
}

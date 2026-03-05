package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.dto.CompanyDetailsDto;
import com.payvance.erp_saas.core.entity.CompanyDetails;
import com.payvance.erp_saas.core.repository.CompanyDetailsRepository;
import com.payvance.erp_saas.core.repository.TenantRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyDetailsService {

    private final CompanyDetailsRepository companyDetailsRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
 // Handles upsert operation for company details
    @Transactional
    public CompanyDetailsDto upsert(CompanyDetailsDto dto) {
        CompanyDetails companyDetails = new CompanyDetails();
        // Check if company profile already exists for tenant
        if (dto.getTenantId() != null) {
            companyDetails = companyDetailsRepository.findByTenants_Id(dto.getTenantId())
                    .orElse(new CompanyDetails());
         // Validate and map tenant reference
            companyDetails.setTenants(tenantRepository.findById(dto.getTenantId()).orElseThrow(() -> new RuntimeException("Tenant not found")));
        }
       // Validate and map user reference
        if (dto.getUserId() != null) {
            companyDetails.setUser(userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found")));
        }
        // Map company profile fields
        companyDetails.setCompanyName(dto.getCompanyName());
        companyDetails.setAddress(dto.getAddress());
        companyDetails.setPinCode(dto.getPinCode());
        companyDetails.setState(dto.getState());
        companyDetails.setCountry(dto.getCountry());
        companyDetails.setGstNumber(dto.getGstNumber());
        // Save and return DTO response
        return mapToDto(companyDetailsRepository.save(companyDetails));
    }
   // Fetch company details based on tenant ID
    @Transactional(readOnly = true)
    public CompanyDetailsDto getByTenantId(Long tenantId) {
    	// Search company details using tenant ID ,Convert entity to DTO if record is found
        return companyDetailsRepository.findByTenants_Id(tenantId)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("CompanyDetails not found for tenant: " + tenantId));
    }
    // Convert CompanyDetails entity into CompanyDetailsDto response object
    private CompanyDetailsDto mapToDto(CompanyDetails entity) {
        if (entity == null) return null;
        CompanyDetailsDto dto = new CompanyDetailsDto();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenants() != null ? entity.getTenants().getId() : null);
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        dto.setCompanyName(entity.getCompanyName());
        dto.setAddress(entity.getAddress());
        dto.setPinCode(entity.getPinCode());
        dto.setState(entity.getState());
        dto.setCountry(entity.getCountry());
        dto.setGstNumber(entity.getGstNumber());
        return dto;
    }
}

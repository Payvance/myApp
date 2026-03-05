package com.payvance.erp_saas.core.controller;


import com.payvance.erp_saas.core.dto.CompanyDetailsDto;
import com.payvance.erp_saas.core.entity.CompanyDetails;
import com.payvance.erp_saas.core.service.CompanyDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company-details")
@RequiredArgsConstructor
public class CompanyDetailsController {

    private final CompanyDetailsService service;

    @PostMapping("/upsert")
    public ResponseEntity<CompanyDetailsDto> upsert(
            @RequestBody CompanyDetailsDto dto) {
        return ResponseEntity.ok(service.upsert(dto));
    }

    @GetMapping("/tenant")
    public ResponseEntity<CompanyDetailsDto> getByTenant(
            @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getByTenantId(tenantId));
    }

}
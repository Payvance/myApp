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
 * Aniket Desai  	 1.0.0       06-Jan-2026    class created
 *
 **/
package com.payvance.erp_saas.core.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.dto.ActivationKeyListDTO;
import com.payvance.erp_saas.core.dto.ActivationKeyResponseDto;
import com.payvance.erp_saas.core.dto.ActivationKeySearchRequestDto;
import com.payvance.erp_saas.core.dto.ApprovedBatchDropdownDto;
import com.payvance.erp_saas.core.dto.IssueLicenseRequestDto;
import com.payvance.erp_saas.core.dto.LicenseCheckDto;
import com.payvance.erp_saas.core.dto.VendorActivationBatchResponseDTO;
import com.payvance.erp_saas.core.dto.VendorBatchRequestDto;
import com.payvance.erp_saas.core.dto.VendorBatchResponseDto;
import com.payvance.erp_saas.core.dto.VendorBatchSearchRequestDto;
import com.payvance.erp_saas.core.dto.VendorLicenseStatusUpdateRequest;
import com.payvance.erp_saas.core.entity.ActivationKey;
import com.payvance.erp_saas.core.entity.VendorActivationBatch;
import com.payvance.erp_saas.core.repository.TenantRepository;
import com.payvance.erp_saas.core.repository.VendorRepository;
import com.payvance.erp_saas.core.service.ActivationKeyService;
import com.payvance.erp_saas.core.service.PlanPriceService;
import com.payvance.erp_saas.core.service.PlanService;
import com.payvance.erp_saas.core.service.TenantService;
import com.payvance.erp_saas.core.service.VendorDiscountService;
import com.payvance.erp_saas.core.service.VendorLicenseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vendor/licenses")
@RequiredArgsConstructor
public class VendorLicenseController {

    private final VendorLicenseService vendorLicenseService;

    private final TenantService licenseService;

	private final TenantRepository tenantRepository;
    
    private final PlanPriceService planPriceService;

    private final VendorDiscountService vendorDiscountService;

    private final ActivationKeyService activationKeyService;

    private final PlanService planService;

    private final VendorRepository vendorRepository;


    @PostMapping("/batches")
    public ResponseEntity<VendorBatchResponseDto> createBatch(@Valid @RequestBody VendorBatchRequestDto request) {
        VendorActivationBatch entity = vendorLicenseService.createBatch(request);
        VendorBatchResponseDto dto = new VendorBatchResponseDto();
        BeanUtils.copyProperties(entity, dto);
        return ResponseEntity.ok(dto);
    }

    /**
     * API to update vendor license batch status
     *
     * URL : PUT /api/vendor/licenses/batches/status
     * Usage: Frontend sends batchId, status, and userId in request body
     *
     * @return Success response with updated status
     */
    @PutMapping("/batches/status")
    public ResponseEntity<Map<String, Object>> updateLicenseStatus(
            @RequestBody VendorLicenseStatusUpdateRequest request) {

        Map<String, Object> response = vendorLicenseService.updateLicenseStatus(
                request.getBatchId(),
                request.getStatus(),
                request.getUserId());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/batches/{id}")
    public ResponseEntity<VendorBatchResponseDto> updateBatch(@PathVariable Long id,
            @Valid @RequestBody VendorBatchRequestDto request) {
        VendorActivationBatch entity = vendorLicenseService.updateBatch(id, request);
        VendorBatchResponseDto dto = new VendorBatchResponseDto();
        BeanUtils.copyProperties(entity, dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/batches/{id}")
    public ResponseEntity<VendorBatchResponseDto> getBatchById(@PathVariable Long id) {
        return ResponseEntity.ok(vendorLicenseService.getBatchById(id));
    }

    @GetMapping("/batches")
    public ResponseEntity<Page<VendorBatchResponseDto>> searchBatches(
            VendorBatchSearchRequestDto request,
            @RequestParam(required = false) Long vendorId,
            Pageable pageable) {
        return ResponseEntity.ok(
                vendorLicenseService.searchBatches(request, vendorId, pageable));
    }

    @GetMapping("/keys")
    public ResponseEntity<Page<ActivationKeyResponseDto>> searchKeys(ActivationKeySearchRequestDto request,
            @RequestParam(required = false) Long vendorBatchId) {
        Page<ActivationKey> page = vendorLicenseService.searchActivationKeys(request, vendorBatchId);
        Page<ActivationKeyResponseDto> dtoPage = page.map(entity -> {
            ActivationKeyResponseDto dto = new ActivationKeyResponseDto();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        });
        return ResponseEntity.ok(dtoPage);
    }

    /*
     * * Get Active Plans for Dropdown
     */

    @GetMapping("/active/plans")
    public List<Map<String, Object>> getActivePlans() {
        return planService.getActivePlansForDropdown();
    }

    /*
     * Get Plan Price by Plan Id
     */
    @GetMapping("/plan/price")
    public Map<String, Object> getPlanPrice(@RequestParam("code") Long planId) {

        BigDecimal amount = planPriceService.getActivePlanAmount(planId);

        return Map.of("planId", planId, "amount", amount);
    }

    /**
     * Get the latest discount type and value
     */

    @GetMapping("/vendor/{userId}/discount")
    public Map<String, Object> getVendorDiscount(@PathVariable Long userId) {
        return vendorDiscountService.getDiscount(userId);
    }

    /*
     * Get All Activation Keys with Pagination
     */
    @GetMapping("/assign/licenses")
    public ResponseEntity<Page<ActivationKeyListDTO>> getAllKeys(
            Pageable pageable) {
        return ResponseEntity.ok(activationKeyService.getAllKeys(pageable));

    }

    /*
     * Get All Vendor Activation Batches with Pagination
     */
    @GetMapping("/license/inventory")
    public ResponseEntity<Page<VendorActivationBatchResponseDTO>> getAll(
            Pageable pageable) {
        return ResponseEntity.ok(vendorLicenseService.getAll(pageable));
    }

    // Admin API
    @PatchMapping("/batches/{id}/approve")
    public ResponseEntity<VendorBatchResponseDto> approveBatch(@PathVariable Long id) {
        VendorActivationBatch entity = vendorLicenseService.approveBatch(id);
        VendorBatchResponseDto dto = new VendorBatchResponseDto();
        BeanUtils.copyProperties(entity, dto);
        return ResponseEntity.ok(dto);
    }

    // Dropdown for Vendor
    @GetMapping("/batches/approved")
    public ResponseEntity<List<ApprovedBatchDropdownDto>> getApprovedBatches(@RequestParam Long vendorId) {
        return ResponseEntity.ok(vendorLicenseService.getApprovedBatchesForDropdown(vendorId));
    }

    // Issue License
    @PostMapping("/issue")
    public ResponseEntity<ActivationKeyResponseDto> issueLicense(@Valid @RequestBody IssueLicenseRequestDto request) {
        ActivationKey entity = vendorLicenseService.issueLicense(request);
        ActivationKeyResponseDto dto = new ActivationKeyResponseDto();
        BeanUtils.copyProperties(entity, dto);
      
        // Set redeemed tenant name if applicable
        if (entity.getRedeemedTenantId() != null) {
            String tenantName = tenantRepository
                    .findTenantNameById(entity.getRedeemedTenantId())
                    .orElse(null);

            dto.setRedeemedTenantName(tenantName);
        }
        return ResponseEntity.ok(dto);
    }

    /*
     * License Check API for SaaS application to validate license
     */
    @PostMapping("/check")
    public ResponseEntity<LicenseCheckDto> check(
            @RequestBody LicenseCheckDto dto) {

        return ResponseEntity.ok(licenseService.check(dto));
    }

}

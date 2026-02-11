package com.payvance.erp_saas.core.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.dto.IdRequest;
import com.payvance.erp_saas.core.dto.VendorDiscountRequest;
import com.payvance.erp_saas.core.dto.VendorDiscountResponse;
import com.payvance.erp_saas.core.service.VendorDiscountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vendor-discount")
@RequiredArgsConstructor
public class VendorDiscountController {

    private final VendorDiscountService vendorDiscountService;

    /**
     * Upsert a vendor discount
     */
    @PostMapping("/upsert")
    public ResponseEntity<VendorDiscountResponse> upsertDiscount(
            @RequestBody VendorDiscountRequest request) {
        VendorDiscountResponse response = vendorDiscountService.upsertDiscount(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all vendor discounts
     */
    @GetMapping("/all")
    public ResponseEntity<Page<VendorDiscountResponse>> getAllDiscounts(Pageable pageable) {
        Page<VendorDiscountResponse> discounts = vendorDiscountService.getAllDiscounts(pageable);
        return ResponseEntity.ok(discounts);
    }
    
    
    /**
     * Fetch vendor discount by ID
     */
    @PostMapping("/get")
    public ResponseEntity<VendorDiscountResponse> getDiscountById(
            @RequestBody IdRequest request
    ) {
        return ResponseEntity.ok(
                vendorDiscountService.getDiscountById(request.getId())
        );
    }

  

}

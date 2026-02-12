package com.payvance.erp_saas.core.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.dto.CouponCheckRequest;
import com.payvance.erp_saas.core.dto.CouponCheckResponse;
import com.payvance.erp_saas.core.dto.CouponRequest;
import com.payvance.erp_saas.core.dto.CouponResponse;
import com.payvance.erp_saas.core.dto.IdRequest;
import com.payvance.erp_saas.core.service.CouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/super-admin/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /**
     * Create or Update Coupon
     * Super Admin API
     */
    @PostMapping
    public CouponResponse upsertCoupon(@RequestBody CouponRequest request) {
        return couponService.upsertCoupon(request);
    }

    /**
     * Fetch all coupons
     * Super Admin API
     */
    @GetMapping
    public ResponseEntity<Page<CouponResponse>> getAllCoupons(Pageable pageable) {
        return ResponseEntity.ok(couponService.getAllCoupons(pageable));
    }
    
    /**
     * Fetch coupon by ID
     */
    @PostMapping("/get")
    public ResponseEntity<CouponResponse> getCouponById(
            @RequestBody IdRequest request
    ) {
        return ResponseEntity.ok(
                couponService.getCouponById(request.getId())
        );
    }

    /**
     * Check coupon availability for a tenant
     */
    @PostMapping("/checkcoupon")
    public ResponseEntity<CouponCheckResponse> checkCouponAvailability(
            @RequestBody CouponCheckRequest request
    ) {
        return ResponseEntity.ok(
                couponService.checkCouponAvailability(request)
        );
    }

}

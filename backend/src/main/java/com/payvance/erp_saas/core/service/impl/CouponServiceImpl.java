package com.payvance.erp_saas.core.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payvance.erp_saas.core.dto.CouponCheckRequest;
import com.payvance.erp_saas.core.dto.CouponCheckResponse;
import com.payvance.erp_saas.core.dto.CouponRequest;
import com.payvance.erp_saas.core.dto.CouponResponse;
import com.payvance.erp_saas.core.entity.Coupon;
import com.payvance.erp_saas.core.repository.CouponRedemptionRepository;
import com.payvance.erp_saas.core.repository.CouponRepository;
import com.payvance.erp_saas.core.repository.TenantUserRoleRepository;
import com.payvance.erp_saas.core.service.CouponService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;

    @Override
    @Transactional
    public CouponResponse upsertCoupon(CouponRequest request) {

        Coupon coupon = (request.getId() != null)
                ? couponRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("Coupon not found"))
                : new Coupon();

        coupon.setCode(request.getCode());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountPercentage(request.getDiscountPercentage());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setCurrency(request.getCurrency());
        coupon.setValidFrom(request.getValidFrom());
        coupon.setValidTo(request.getValidTo());
        coupon.setMaxUses(request.getMaxUses());
        coupon.setDiscription(request.getDiscription());
        coupon.setStatus(request.getStatus());

        return mapToResponse(couponRepository.save(coupon));
    }

    @Override
    public Page<CouponResponse> getAllCoupons(Pageable pageable) {

        Page<Coupon> couponPage = couponRepository.findAll(pageable);

        return couponPage.map(this::mapToResponse);
    }


    private CouponResponse mapToResponse(Coupon coupon) {
        CouponResponse response = new CouponResponse();
        response.setId(coupon.getId());
        response.setCode(coupon.getCode());
        response.setDiscountType(coupon.getDiscountType());
        response.setDiscountPercentage(coupon.getDiscountPercentage());
        response.setDiscountValue(coupon.getDiscountValue());
        response.setCurrency(coupon.getCurrency());
        response.setValidFrom(coupon.getValidFrom());
        response.setValidTo(coupon.getValidTo());
        response.setMaxUses(coupon.getMaxUses());
        response.setUsedCount(coupon.getUsedCount());
        response.setDiscription(coupon.getDiscription());
        response.setStatus(coupon.getStatus());
        response.setCreatedAt(coupon.getCreatedAt());
        response.setUpdatedAt(coupon.getUpdatedAt());
        return response;
    }
    
    
    @Override
    public CouponResponse getCouponById(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + id));
        return mapToResponse(coupon);
    }

    @Override
    public CouponCheckResponse checkCouponAvailability(CouponCheckRequest request) {
        // Resolve tenantId from userId
        List<Long> tenantIds = tenantUserRoleRepository.findTenantIdsByUserId(request.getUserId());
        if (tenantIds == null || tenantIds.isEmpty()) {
            return CouponCheckResponse.builder()
                    .available(false)
                    .message("User is not associated with any tenant")
                    .build();
        }
        Long tenantId = tenantIds.get(0); // Assuming primary tenant

        Coupon coupon = couponRepository.findByCode(request.getCode())
                .orElse(null);

        if (coupon == null) {
            return CouponCheckResponse.builder()
                    .available(false)
                    .message("Invalid coupon code")
                    .build();
        }

        // 1. Check status
        if (!"ACTIVE".equalsIgnoreCase(coupon.getStatus())) {
            return CouponCheckResponse.builder()
                    .available(false)
                    .message("Coupon is not active")
                    .build();
        }

        // 2. Check validity period
        LocalDate today = LocalDate.now();
        if (coupon.getValidFrom() != null && today.isBefore(coupon.getValidFrom())) {
            return CouponCheckResponse.builder()
                    .available(false)
                    .message("Coupon validity period starts on " + coupon.getValidFrom())
                    .build();
        }
        if (coupon.getValidTo() != null && today.isAfter(coupon.getValidTo())) {
            return CouponCheckResponse.builder()
                    .available(false)
                    .message("Coupon expired on " + coupon.getValidTo())
                    .build();
        }

        // 3. Check max uses
        if (coupon.getMaxUses() != null && coupon.getUsedCount() != null && coupon.getUsedCount() >= coupon.getMaxUses()) {
            return CouponCheckResponse.builder()
                    .available(false)
                    .message("Coupon usage limit reached")
                    .build();
        }

        // 4. Check if tenant has already used it
        if (couponRedemptionRepository.existsByCouponIdAndTenantId(coupon.getId(), tenantId)) {
            return CouponCheckResponse.builder()
                    .available(false)
                    .message("You have already used this coupon")
                    .build();
        }

        return CouponCheckResponse.builder()
                .available(true)
                .message("Coupon is available")
                .discountValue(coupon.getDiscountValue())
                .discountType(coupon.getDiscountType())
                .currency(coupon.getCurrency())
                .build();
    }
}

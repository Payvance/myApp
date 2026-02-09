package com.payvance.erp_saas.core.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payvance.erp_saas.core.dto.CouponRequest;
import com.payvance.erp_saas.core.dto.CouponResponse;
import com.payvance.erp_saas.core.entity.Coupon;
import com.payvance.erp_saas.core.repository.CouponRepository;
import com.payvance.erp_saas.core.service.CouponService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

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

   
}

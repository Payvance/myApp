package com.payvance.erp_saas.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.payvance.erp_saas.core.dto.CouponRequest;
import com.payvance.erp_saas.core.dto.CouponResponse;

public interface CouponService {

    CouponResponse upsertCoupon(CouponRequest request);

    Page<CouponResponse> getAllCoupons(Pageable pageable);

    
    CouponResponse getCouponById(Long id);

   
}

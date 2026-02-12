package com.payvance.erp_saas.core.repository;

import com.payvance.erp_saas.core.entity.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {

    boolean existsByCouponIdAndTenantId(Long couponId, Long tenantId);
}

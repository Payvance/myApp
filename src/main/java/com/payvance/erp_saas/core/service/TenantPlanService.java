package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.dto.TenantPlanResponse;

public interface TenantPlanService {

    TenantPlanResponse getTenantPlans(Long tenantId);
}

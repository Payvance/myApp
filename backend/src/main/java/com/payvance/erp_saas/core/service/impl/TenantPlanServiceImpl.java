package com.payvance.erp_saas.core.service.impl;

import com.payvance.erp_saas.core.dto.*;
import com.payvance.erp_saas.core.entity.Subscription;
import com.payvance.erp_saas.core.entity.Tenant;
import com.payvance.erp_saas.core.repository.PlanRepository;
import com.payvance.erp_saas.core.repository.SubscriptionRepository;
import com.payvance.erp_saas.core.repository.TenantRepository;
import com.payvance.erp_saas.core.service.TenantPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TenantPlanServiceImpl implements TenantPlanService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final TenantRepository tenantRepository;

    @Override
public TenantPlanResponse getTenantPlans(Long tenantId) {

    // 1 Active subscription
    Optional<Subscription> subscriptionOpt =
            subscriptionRepository.findActiveSubscription(tenantId);

    CurrentPlanResponse currentPlan = subscriptionOpt.map(s ->
            new CurrentPlanResponse(
                    s.getId(),
                    s.getPlan().getId(),
                    s.getPlan().getCode(),
                    s.getPlan().getName(),
                    s.getStatus(),
                    s.getStartAt(),
                    s.getCurrentPeriodEnd()
            )
    ).orElse(null);

    // 2 Available plans (include limitations and prices)
    List<PlanResponse> plans =
            planRepository.findByIsActiveTrue()
                    .stream()
                    .map(p -> {
                        com.payvance.erp_saas.core.entity.PlanLimitation pl = p.getPlanLimitation();
                        com.payvance.erp_saas.core.entity.PlanPrice pp = p.getPlanPrice();

                        PlanLimitationResponse plResp = pl != null ? new PlanLimitationResponse(
                                pl.getId(),
                                pl.getAllowedUserCount(),
                                pl.getAllowedCompanyCount(),
                                pl.getAllowedUserCountTill(),
                                pl.getAllowedCompanyCountTill(),
                                pl.getCreatedAt(),
                                pl.getUpdatedAt()
                        ) : null;

                        PlanPriceResponse ppResp = pp != null ? new PlanPriceResponse(
                                pp.getId(),
                                pp.getBillingPeriod(),
                                pp.getCurrency(),
                                pp.getAmount(),
                                pp.getIsActive(),
                                pp.getCreatedAt(),
                                pp.getUpdatedAt()
                        ) : null;

                        return new PlanResponse(
                                p.getId(),
                                p.getCode(),
                                p.getName(),
                                p.getIsActive(),
                                plResp,
                                ppResp
                        );
                    })
                    .toList();

    // 3 Fetch tenant data for the given tenantId (including trial info)
    List<TenantResponse> tenants = tenantRepository.findById(tenantId)
            .stream()
            .map(t -> new TenantResponse(
                    t.getId(),
                    t.getEmail(),
                    t.getName(),
                    t.getPhone(),
                    t.getStatus(),
                    t.getCreatedAt(),
                    t.getTrialStartAt(),
                    t.getTrialEndAt()
            ))
            .toList();

    return new TenantPlanResponse(tenants, currentPlan, plans);
}
                    
}

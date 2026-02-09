package com.payvance.erp_saas.core.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.payvance.erp_saas.core.entity.PlanPrice;
import com.payvance.erp_saas.core.repository.PlanPriceRepository;

@Service
public class PlanPriceService {
	
	
	private final PlanPriceRepository planPriceRepository;

	public PlanPriceService(PlanPriceRepository planPriceRepository) {
		this.planPriceRepository = planPriceRepository;
	}
	public BigDecimal getActivePlanAmount(Long planId) {

        return planPriceRepository
                .findByPlan_IdAndIsActive(planId, (byte) 1)
                .map(PlanPrice::getAmount)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Active price not found for plan id: " + planId
                        )
                );
    }

}

package com.payvance.erp_saas.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.payvance.erp_saas.core.dto.AddonResponse;
import com.payvance.erp_saas.core.dto.TenantPlanUsageResponse;
import com.payvance.erp_saas.core.entity.AddOn;
import com.payvance.erp_saas.core.repository.SubscriptionAddonRepository;
import com.payvance.erp_saas.core.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenantPlanUsageService {
	
	 private final SubscriptionRepository subscriptionRepository;
	 private final SubscriptionAddonRepository subscriptionAddonRepository;

	 
	 /*
	  * Get paginated tenant plan usage with addons
	  */
	 public Page<TenantPlanUsageResponse> getTenantPlanUsage(
		        Long tenantId,
		        Pageable pageable
		) {

		    Page<TenantPlanUsageResponse> page =
		            subscriptionRepository.findTenantPlanUsageByTenantId(tenantId, pageable);

		    page.forEach(response -> {
		        List<AddOn> addons =
		                subscriptionAddonRepository.findActiveAddonsBySubscriptionId(
		                        response.getSubscriptionId()
		                );

		        List<AddonResponse> addonResponses = addons.stream()
		                .map(a -> new AddonResponse(
		                        a.getId(),
		                        a.getCode(),
		                        a.getName(),
		                        a.getUnit(),
		                        a.getUnitPrice(),
		                        a.getStatus()
		                ))
		                .toList();

		        response.setAddons(addonResponses);
		    });

		    return page;
		}



}

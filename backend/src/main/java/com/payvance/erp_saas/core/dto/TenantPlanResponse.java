package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TenantPlanResponse {
        
    private List<TenantResponse> tenants;  //  include tenant details if needed
    private CurrentPlanResponse currentPlan;
    private List<PlanResponse> availablePlans;
    
}

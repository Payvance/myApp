package com.payvance.erp_saas.core.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllTenantsNestedResponse {
    
    private List<TenantUserNestedResponse> tenants;
}

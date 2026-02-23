package com.payvance.erp_saas.core.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantUserNestedResponse {
    
    private Long tenantId;
    private String name;
    private String email;
    private String phone;
    private Long roleId;
    private Boolean active;
    private List<UserNestedData> nestedData;
    

}

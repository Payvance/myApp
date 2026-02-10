package com.payvance.erp_saas.erp.dto;

import com.payvance.erp_saas.erp.entity.DropdownCategory;

import lombok.Data;

@Data
public class DropdownRequest {
	private Long tenantId;
    private String companyId;
    private DropdownCategory category;
    
    
    // OPTIONAL — only used in duplicate check API
    private String name;

}

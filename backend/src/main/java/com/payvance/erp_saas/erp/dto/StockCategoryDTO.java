package com.payvance.erp_saas.erp.dto;

import lombok.Data;

@Data
public class StockCategoryDTO {
    private String guid;
    private String name;
    private String parentGuid;
    private String parentName;
    private String companyGuid;
    private String companyName;
}

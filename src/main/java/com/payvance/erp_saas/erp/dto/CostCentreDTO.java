package com.payvance.erp_saas.erp.dto;

import lombok.Data;

@Data
public class CostCentreDTO {
    private String guid;
    private String name;
    private String categoryName;
    private Boolean isReserved;
    private String companyGuid;
    private String companyName;
}

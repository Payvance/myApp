package com.payvance.erp_saas.erp.dto;

import lombok.Data;

@Data
public class GodownDTO {
    private String guid;
    private String name;
    private String parentGuid;
    private String parentName;
    private Boolean isReserved;
    private String companyGuid;
    private String companyName;
}

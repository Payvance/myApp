package com.payvance.erp_saas.erp.dto;

import lombok.Data;

@Data
public class TaxUnitDTO {
    private String guid;
    private String name;
    private String taxType;
    private String registrationNumber;
    private String companyGuid;
    private String companyName;
}

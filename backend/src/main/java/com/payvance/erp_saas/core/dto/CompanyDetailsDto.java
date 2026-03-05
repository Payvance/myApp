package com.payvance.erp_saas.core.dto;

import lombok.Data;

@Data
public class CompanyDetailsDto {
    private Long id;
    private Long tenantId;
    private Long userId;
    private String companyName;
    private String address;
    private String pinCode;
    private String state;
    private String country;
    private String gstNumber;
}

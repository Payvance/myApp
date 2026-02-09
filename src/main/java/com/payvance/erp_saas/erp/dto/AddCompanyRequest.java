package com.payvance.erp_saas.erp.dto;

import lombok.Data;

@Data
public class AddCompanyRequest {
    private String name;
    private String guid;
    private String booksFrom;
    private String gstin;
    private String timezone;
    private Integer financialYear;
    private String financialYearFrom;
    private String financialYearTo;
    private String pan;
    private String phone;
}

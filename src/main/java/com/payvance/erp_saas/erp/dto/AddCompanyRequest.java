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

    // New Fields
    private String lastVoucherDate;
    private Boolean isSeparateActualBilledQty;
    private Boolean isDiscountApplicable;
    private Boolean isCostCenterOn;
    private Boolean isInventoryOn;
    private Boolean isAccountingOn;
    private Boolean isPayrollOn;
    private String cinNumber;
    private Boolean isBillWiseOn;
    private Boolean isBatchesOn;
    private String baseCurrency;
}

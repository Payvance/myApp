package com.payvance.erp_saas.core.dto;

import lombok.Data;

@Data
public class UserUpdateRequestDto {
	/* ================= USER ================= */
    private String name;
    private String email;
    private String phone;
    private Boolean isActive;

    /* ================= CA ================= */
    private String caRegNo;
    private Integer enrollmentYear;
    private String icaiMemberStatus;
    private String practiceType;
    private String firmName;
    private String icaiMemberNo;
    private String caStatus;
    private String caType;

    /* ================= VENDOR ================= */
    private String vendorName;
    private String vendorType;
    private Long vendorDiscountId;
    private Integer experienceYears;
    private String gstNo;
    private String cinNo;
    private String panNo;
    private String tanNo;
    private String aadharNo;
    private String vendorStatus;

    /* ================= ADDRESS ================= */
    private String houseBuildingNo;
    private String houseBuildingName;
    private String roadAreaPlace;
    private String landmark;
    private String village;
    private String taluka;
    private String city;
    private String district;
    private String state;
    private String pincode;
    private String postOffice;
    private String country;

    /* ================= BANK ================= */
    private String bankName;
    private String branchName;
    private String accountNumber;
    private String ifscCode;

}

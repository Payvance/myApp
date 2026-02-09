package com.payvance.erp_saas.core.dto;

import lombok.Data;

@Data
public class CaRequest {
    private Long userId;
    private Long caId; // optional for update
    private String name;
    private String email;
    private String phone;

    // CA specific fields
    private String caRegNo;
    private Integer enrollmentYear;
    private String icaiMemberStatus;
    private String practiceType;
    private String firmName;
    private String icaiMemberNo;
    private String aadharNo;


    // Address fields
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

    // Bank fields
    private String bankName;
    private String branchName;
    private String accountNumber;
    private String ifscCode;

    private String status; // optional
    
    private String gstNo;
    private String cinNo;
    private String panNo;
    private String tanNo;
    private String caType;
}

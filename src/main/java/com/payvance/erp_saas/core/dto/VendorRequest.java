package com.payvance.erp_saas.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorRequest {
    private Long vendorId;       // Optional, for update
    private Long userId;         // Mandatory for both create & update
    private String name;
    private String email;
    private String phone;
    private Long vendorDiscountId;
    private String vendorType;
    private Integer experienceYears;
    private String gstNo;
    private String cinNo;
    private String panNo;
    private String tanNo;
    private String aadharNo;
    private String status;
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
    // Bank details fields
    private String bankName;
    private String branchName;
    private String accountNumber;
    private String ifscCode;
}

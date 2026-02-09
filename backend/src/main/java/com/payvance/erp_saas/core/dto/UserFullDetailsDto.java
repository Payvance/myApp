package com.payvance.erp_saas.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFullDetailsDto {
	// User
	private Long userId;
	private String userName;
	private String userEmail;
	private String userPhone;
	private Boolean isActive;
	private String role;

	// CA
	private Long caId;
	private String caRegNo;
	private Integer enrollmentYear;
	private String icaiMemberStatus;
	private String practiceType;
	private String firmName;
	private String icaiMemberNo;
	private String caStatus;
	private String caType;
	private String caGstNo;
	private String caCinNo;
	private String caPanNo;
	private String caTanNo;
	// Vendor
	private Long vendorId;
	private String vendorName;
	private String vendorType;
	private Long vendorDiscountId;
	private Integer experienceYears;
	private String gstNo;
	private String cinNo;
	private String panNo;
	private String tanNo;
	private String vendorAadharNo;
	private String vendorStatus;

	// Address
	private Long addressId;
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

	// Bank
	private Long bankId;
	private String bankName;
	private String branchName;
	private String accountNumber;
	private String ifscCode;
	public UserFullDetailsDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	public UserFullDetailsDto(Long userId, String userName, String userEmail, String userPhone, Boolean isActive,
			String role, Long caId, String caRegNo, Integer enrollmentYear, String icaiMemberStatus,
			String practiceType, String firmName, String icaiMemberNo, String caStatus, String caType, String caGstNo,
			String caCinNo, String caPanNo, String caTanNo, Long vendorId, String vendorName, String vendorType,
			Long vendorDiscountId, Integer experienceYears, String gstNo, String cinNo, String panNo, String tanNo,
			String vendorAadharNo, String vendorStatus, Long addressId, String houseBuildingNo,
			String houseBuildingName, String roadAreaPlace, String landmark, String village, String taluka, String city,
			String district, String state, String pincode, String postOffice, String country, Long bankId,
			String bankName, String branchName, String accountNumber, String ifscCode) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.userEmail = userEmail;
		this.userPhone = userPhone;
		this.isActive = isActive;
		this.role = role;
		this.caId = caId;
		this.caRegNo = caRegNo;
		this.enrollmentYear = enrollmentYear;
		this.icaiMemberStatus = icaiMemberStatus;
		this.practiceType = practiceType;
		this.firmName = firmName;
		this.icaiMemberNo = icaiMemberNo;
		this.caStatus = caStatus;
		this.caType = caType;
		this.caGstNo = caGstNo;
		this.caCinNo = caCinNo;
		this.caPanNo = caPanNo;
		this.caTanNo = caTanNo;
		this.vendorId = vendorId;
		this.vendorName = vendorName;
		this.vendorType = vendorType;
		this.vendorDiscountId = vendorDiscountId;
		this.experienceYears = experienceYears;
		this.gstNo = gstNo;
		this.cinNo = cinNo;
		this.panNo = panNo;
		this.tanNo = tanNo;
		this.vendorAadharNo = vendorAadharNo;
		this.vendorStatus = vendorStatus;
		this.addressId = addressId;
		this.houseBuildingNo = houseBuildingNo;
		this.houseBuildingName = houseBuildingName;
		this.roadAreaPlace = roadAreaPlace;
		this.landmark = landmark;
		this.village = village;
		this.taluka = taluka;
		this.city = city;
		this.district = district;
		this.state = state;
		this.pincode = pincode;
		this.postOffice = postOffice;
		this.country = country;
		this.bankId = bankId;
		this.bankName = bankName;
		this.branchName = branchName;
		this.accountNumber = accountNumber;
		this.ifscCode = ifscCode;
	}

	
	
}
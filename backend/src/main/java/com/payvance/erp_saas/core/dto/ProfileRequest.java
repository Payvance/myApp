package com.payvance.erp_saas.core.dto;

import lombok.Data;

@Data
public class ProfileRequest {
    // Role id will be provided in the request payload to decide which profile service to invoke
    private Integer roleId;
    private Long userId;
    private String name;
    private String email;
    private String phone;

    // Vendor-specific fields
    private String vendorType;
    private Integer experienceYears;
    private String gstNo;
    private String cinNo;
    private String panNo;
    private String tanNo;
    private String aadharNo;

    // CA-specific fields
    private String caRegNo;
    private Integer enrollmentYear;
    private String icaiMemberStatus;
    private String practiceType;
    private String firmName;
    private String icaiMemberNo;
    private String caType;
    

    private AddressDTO address;
    private BankDTO bank;
    private String status;

    // Convert to VendorRequest
    public VendorRequest toVendorRequest() {
        VendorRequest vendor = new VendorRequest();
        vendor.setUserId(this.userId);
        vendor.setName(this.name);
        vendor.setEmail(this.email);
        vendor.setPhone(this.phone);
        vendor.setVendorType(this.vendorType);
        vendor.setExperienceYears(this.experienceYears);
        vendor.setGstNo(this.gstNo);
        vendor.setCinNo(this.cinNo);
        vendor.setPanNo(this.panNo);
        vendor.setTanNo(this.tanNo);
        vendor.setAadharNo(this.aadharNo);
        vendor.setStatus(this.status);

        if (this.address != null) {
            vendor.setHouseBuildingNo(address.getHouseBuildingNo());
            vendor.setHouseBuildingName(address.getHouseBuildingName());
            vendor.setRoadAreaPlace(address.getRoadAreaPlace());
            vendor.setLandmark(address.getLandmark());
            vendor.setVillage(address.getVillage());
            vendor.setTaluka(address.getTaluka());
            vendor.setCity(address.getCity());
            vendor.setDistrict(address.getDistrict());
            vendor.setState(address.getState());
            vendor.setPincode(address.getPincode());
            vendor.setPostOffice(address.getPostOffice());
            vendor.setCountry(address.getCountry());
        }
        if (this.bank != null) {
            vendor.setBankName(bank.getBankName());
            vendor.setBranchName(bank.getBranchName());
            vendor.setAccountNumber(bank.getAccountNumber());
            vendor.setIfscCode(bank.getIfscCode());
        }
        return vendor;
    }

    // Convert to CaRequest
    public CaRequest toCaRequest() {
        CaRequest ca = new CaRequest();
        ca.setUserId(this.userId);
        ca.setName(this.name);
        ca.setEmail(this.email);
        ca.setPhone(this.phone);
        ca.setCaRegNo(this.caRegNo);
        ca.setEnrollmentYear(this.enrollmentYear);
        ca.setIcaiMemberStatus(this.icaiMemberStatus);
        ca.setPracticeType(this.practiceType);
        ca.setFirmName(this.firmName);
        ca.setIcaiMemberNo(this.icaiMemberNo);
        ca.setAadharNo(this.aadharNo);
        ca.setStatus(this.status);
        ca.setGstNo(this.gstNo);
        ca.setCinNo(this.cinNo);
        ca.setPanNo(this.panNo);
        ca.setTanNo(this.tanNo);
        ca.setCaType(this.caType);

        if (this.address != null) {
            ca.setHouseBuildingNo(address.getHouseBuildingNo());
            ca.setHouseBuildingName(address.getHouseBuildingName());
            ca.setRoadAreaPlace(address.getRoadAreaPlace());
            ca.setLandmark(address.getLandmark());
            ca.setVillage(address.getVillage());
            ca.setTaluka(address.getTaluka());
            ca.setCity(address.getCity());
            ca.setDistrict(address.getDistrict());
            ca.setState(address.getState());
            ca.setPincode(address.getPincode());
            ca.setPostOffice(address.getPostOffice());
            ca.setCountry(address.getCountry());
        }
        if (this.bank != null) {
            ca.setBankName(bank.getBankName());
            ca.setBranchName(bank.getBranchName());
            ca.setAccountNumber(bank.getAccountNumber());
            ca.setIfscCode(bank.getIfscCode());
        }
        return ca;
    }

    @Data
    public static class AddressDTO {
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
    }

    @Data
    public static class BankDTO {
        private String bankName;
        private String branchName;
        private String accountNumber;
        private String ifscCode;
    }
}

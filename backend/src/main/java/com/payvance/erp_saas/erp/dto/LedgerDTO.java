package com.payvance.erp_saas.erp.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class LedgerDTO {
    private String guid;
    private String name;
    private String alias;
    private String companyGuid;
    private String companyName;
    private String groupGuid;
    private String groupName;
    private Boolean isBillwise;
    private Boolean isCostCenter;
    private Boolean isParty;
    private Boolean isRevenue;
    private BigDecimal openingBalance;
    private String openingBalanceType;
    private BigDecimal closingBalance;
    private String gstRegistrationType;
    private String gstin;
    private String pan;
    private String stateName;
    private String countryName;
    private String contactName;
    private String mobile;
    private String email;
    private String emailCC;
    private String fax;
    private String gstPartyContact;
    private String addressLine1;
    private String addressLine2;
    private String pincode;
    private BigDecimal creditLimit;
    private String defaultCreditPeriod;
    private Boolean isReserved;
    private String description;
    private String notes;
    private String narration;
    private String priceLevel;
    private String website;
    private String phoneNumber;
    private String city;
    private String priorStateName;
    private String hsnCode;
    private String hsnDescription;
    private BigDecimal igstRate;
    private BigDecimal cgstRate;
    private BigDecimal sgstRate;
    private BigDecimal cessRate;
    private String bankAccountName;
    private String bankAccountNumber;
    private String ifscCode;
    private String bankBranchName;
    private String bankBSRCode;
    private String swiftCode;

    @Override
    public String toString() {
        return "LedgerDTO [guid=" + guid + ", name=" + name + ", alias=" + alias + ", companyGuid=" + companyGuid
                + ", companyName=" + companyName + ", groupGuid=" + groupGuid + ", groupName=" + groupName
                + ", isBillwise=" + isBillwise + ", isCostCenter=" + isCostCenter + ", isParty=" + isParty
                + ", isRevenue=" + isRevenue + ", openingBalance=" + openingBalance + ", openingBalanceType="
                + openingBalanceType + ", closingBalance=" + closingBalance + ", gstRegistrationType="
                + gstRegistrationType + ", gstin=" + gstin + ", pan=" + pan + ", stateName=" + stateName
                + ", countryName=" + countryName + ", contactName=" + contactName + ", mobile=" + mobile + ", email="
                + email + ", emailCC=" + emailCC + ", fax=" + fax + ", gstPartyContact=" + gstPartyContact
                + ", addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", pincode=" + pincode
                + ", description=" + description + ", notes=" + notes + ", narration=" + narration
                + ", priceLevel=" + priceLevel + ", website=" + website + ", phoneNumber=" + phoneNumber + "]";
    }
}

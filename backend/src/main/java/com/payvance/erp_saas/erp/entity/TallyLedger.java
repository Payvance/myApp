package com.payvance.erp_saas.erp.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tally_ledgers")
@Data
public class TallyLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "guid", unique = true, nullable = false)
    private String guid;

    @Column(name = "name")
    private String name;

    @Column(name = "alias")
    private String alias;

    @Column(name = "group_guid")
    private String groupGuid;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "is_billwise")
    private Boolean isBillwise;

    @Column(name = "is_party")
    private Boolean isParty;

    @Column(name = "is_revenue")
    private Boolean isRevenue;

    @Column(name = "opening_balance")
    private BigDecimal openingBalance;

    @Column(name = "closing_balance")
    private BigDecimal closingBalance;

    @Column(name = "gstin")
    private String gstin;

    @Column(name = "pan")
    private String pan;

    @Column(name = "email")
    private String email;

    @Column(name = "email_cc")
    private String emailCC;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "fax")
    private String fax;

    @Column(name = "gst_party_contact")
    private String gstPartyContact;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "description")
    private String description;

    @Column(name = "notes")
    private String notes;

    @Column(name = "narration")
    private String narration;

    @Column(name = "price_level")
    private String priceLevel;

    @Column(name = "website")
    private String website;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "city")
    private String city;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "prior_state_name")
    private String priorStateName;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "gst_dealer_type")
    private String gstDealerType;

    @Column(name = "hsn_code")
    private String hsnCode;

    @Column(name = "hsn_description")
    private String hsnDescription;

    @Column(name = "igst_rate")
    private BigDecimal igstRate;

    @Column(name = "cgst_rate")
    private BigDecimal cgstRate;

    @Column(name = "sgst_rate")
    private BigDecimal sgstRate;

    @Column(name = "cess_rate")
    private BigDecimal cessRate;

    @Column(name = "bank_account_name")
    private String bankAccountName;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "bank_branch_name")
    private String bankBranchName;

    @Column(name = "bank_bsr_code")
    private String bankBSRCode;

    @Column(name = "swift_code")
    private String swiftCode;

    @Column(name = "is_cost_center")
    private Boolean isCostCenter;
}

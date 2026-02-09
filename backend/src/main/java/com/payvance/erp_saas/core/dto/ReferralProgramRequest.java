package com.payvance.erp_saas.core.dto;

import lombok.Data;

@Data
public class ReferralProgramRequest {

    private Long id;                 // optional (for update)
    private String code;              // REFERRAL CODE
    private String ownerType;         // CA / TENANT
    private String name;

    private String rewardType;        // FLAT / PERCENTAGE
    private Double rewardValue;       // for FLAT
    private Double rewardPercentage;  // for PERCENTAGE

    private Double maxPerReferrer;    // only for FLAT
    private String status;            // ACTIVE / INACTIVE
}

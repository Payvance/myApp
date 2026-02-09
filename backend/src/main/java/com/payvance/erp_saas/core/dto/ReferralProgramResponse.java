package com.payvance.erp_saas.core.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReferralProgramResponse {

    private Long id;
    private String code;
    private String ownerType;
    private String name;

    private String rewardType;
    private Double rewardValue;
    private Double rewardPercentage;

    private Double maxPerReferrer;
    private String status;
}

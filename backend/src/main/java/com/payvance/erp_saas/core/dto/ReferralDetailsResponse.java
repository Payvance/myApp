package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReferralDetailsResponse {
	
	// Referral code
    private String referralCode;

    // Program info
    private Long programId;
    private String programCode;   
    private String programName;
    private String rewardType;
    private Double rewardValue;
    private Double rewardPercentage;
    private String programStatus;

}

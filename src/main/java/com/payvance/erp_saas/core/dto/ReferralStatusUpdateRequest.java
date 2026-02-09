package com.payvance.erp_saas.core.dto;

import lombok.Data;

/**
 * Request body for updating referral program status
 */
@Data
public class ReferralStatusUpdateRequest {

    private Long referralId;
    private String status;
}

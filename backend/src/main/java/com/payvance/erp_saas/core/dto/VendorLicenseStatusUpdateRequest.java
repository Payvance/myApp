package com.payvance.erp_saas.core.dto;

import lombok.Data;

/**
 * Request body for updating vendor license batch status
 */
@Data
public class VendorLicenseStatusUpdateRequest {

    private Long batchId;
    private String status;
    private Long userId;
}

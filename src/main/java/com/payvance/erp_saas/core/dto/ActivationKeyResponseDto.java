/**
 * Copyright: Â© 2024 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of Payvance Innovation Pvt. Ltd.
 * Payvance Innovation Pvt. Ltd. retains all title to and intellectual property rights in these materials.
 *
 **/

/**
 *
 * @author           version     date        change description
 * Aniket Desai  	 1.0.0       06-Jan-2026    class created
 *
 **/
package com.payvance.erp_saas.core.dto;

import com.payvance.erp_saas.core.entity.ActivationKey;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivationKeyResponseDto {
    private Long id;
    private Long vendorBatchId;
    private String activationCodeHash; // Maybe don't expose hash? Exposure asked for "plan" (last4?)
    private String plainCodeLast4;
    private ActivationKey.Status status;
    private String issuedToEmail;
    private String issuedToPhone;
    private Long redeemedTenantId;
    private String redeemedTenantName;
    private LocalDateTime redeemedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}

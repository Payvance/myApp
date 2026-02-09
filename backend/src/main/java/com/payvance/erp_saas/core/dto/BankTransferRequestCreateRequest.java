package com.payvance.erp_saas.core.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for creating bank transfer request
 */
@Data
public class BankTransferRequestCreateRequest {

    private Long referralsCount;     // total referrals count
    private BigDecimal amount;               // total amount to transfer
    private List<Long> referralIds;      // referral IDs to be linked
}

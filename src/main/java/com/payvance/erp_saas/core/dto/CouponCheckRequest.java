package com.payvance.erp_saas.core.dto;

import lombok.Data;

@Data
public class CouponCheckRequest {
    private String code;
    private Long userId;
}

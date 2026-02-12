package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponCheckResponse {
    private boolean available;
    private String message;
    private Double discountValue;
    private String discountType;
    private String currency;
}

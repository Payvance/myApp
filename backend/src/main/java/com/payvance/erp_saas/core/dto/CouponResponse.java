package com.payvance.erp_saas.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class CouponResponse {

    private Long id;

    private String code;

    private String discountType;

    private Double discountPercentage;

    private Double discountValue;

    private String currency;

    private LocalDate validFrom;

    private LocalDate validTo;

    private Long maxUses;

    private Long usedCount;

    private String discription;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

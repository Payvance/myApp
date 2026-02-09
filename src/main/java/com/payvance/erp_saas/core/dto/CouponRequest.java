package com.payvance.erp_saas.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CouponRequest {

    private Long id; // optional → if present = update, else create

    private String code;

    private String discountType; // FLAT / PERCENTAGE

    private Double discountPercentage;

    private Double discountValue;

    private String currency;

    private LocalDate validFrom;

    private LocalDate validTo;

    private Long maxUses;

    private String discription;

    private String status; // ACTIVE / INACTIVE
}

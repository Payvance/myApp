package com.payvance.erp_saas.core.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VendorDiscountRequest {
    private Long id; // Optional, for update
    private String type; // flat / percentage
    private String name;
    private Double value;
    private LocalDate effectiveDate; // optional
}

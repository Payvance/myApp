package com.payvance.erp_saas.core.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class VendorDiscountResponse {
    private Long id;
    private String type;
    private String name;
    private Double value;
    private LocalDate effectiveDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

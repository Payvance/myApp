package com.payvance.erp_saas.core.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddonResponse {
	private Long addonId;
    private String code;
    private String name;
    private String unit;
    private BigDecimal unitPrice;
    private String status;

}

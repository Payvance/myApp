package com.payvance.erp_saas.core.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddOnResponseDto {
	private Long id;
    private String code;
    private String name;
    private String currency;
    private String unit;
    private BigDecimal unitPrice;
    private String status;
    private Long planId;

}

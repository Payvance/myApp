package com.payvance.erp_saas.erp.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class UnitDTO {
    private String companyGuid;
    private String guid;
    private String name;
    private String symbol;
    private Integer decimalPlaces;
    private Boolean isCompound;
    private String firstUnit;
    private String secondUnit;
    private BigDecimal conversion;
}

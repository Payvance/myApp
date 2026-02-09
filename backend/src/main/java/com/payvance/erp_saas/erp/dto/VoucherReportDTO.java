package com.payvance.erp_saas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherReportDTO {
    private String name;
    private java.math.BigDecimal quantity;
    private java.math.BigDecimal amount;
}

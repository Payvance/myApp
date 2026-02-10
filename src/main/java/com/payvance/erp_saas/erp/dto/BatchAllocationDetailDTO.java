package com.payvance.erp_saas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchAllocationDetailDTO {
    private String godownName;
    private String batchName;
    private String actualQty;
    private String billedQty;
    private java.math.BigDecimal rate;
    private java.math.BigDecimal amount;
}

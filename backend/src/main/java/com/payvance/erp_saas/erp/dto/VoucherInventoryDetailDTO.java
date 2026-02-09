package com.payvance.erp_saas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherInventoryDetailDTO {
    private String stockItemName;
    private String actualQty;
    private String billedQty;
    private String rate;
    private BigDecimal amount;
    private List<BatchAllocationDetailDTO> batchAllocations;
}

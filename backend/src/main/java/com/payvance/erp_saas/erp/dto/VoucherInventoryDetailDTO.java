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
    private BigDecimal rate;
    private BigDecimal amount;

    // GST & UOM
    private String hsnCode;
    private BigDecimal gstRate;
    private String uom;
    private BigDecimal cgstAmount;
    private BigDecimal sgstAmount;
    private BigDecimal igstAmount;

    private List<BatchAllocationDetailDTO> batchAllocations;
}

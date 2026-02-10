package com.payvance.erp_saas.erp.dto;

import lombok.Data;

@Data
public class VoucherInventoryDTO {
    private String stockItemGuid;
    private String stockItemName;
    private String actualQty;
    private java.math.BigDecimal actualQtyNum;
    private String billedQty;
    private java.math.BigDecimal rate;
    private java.math.BigDecimal amount;
    private String hsnCode;
    private String gstTaxability;
    private java.math.BigDecimal gstRate;

    // Classification & Tax Amounts
    private String uom;
    private java.math.BigDecimal cgstAmount;
    private java.math.BigDecimal sgstAmount;
    private java.math.BigDecimal igstAmount;

    private java.util.List<VoucherBatchAllocationDTO> batchAllocations;
}

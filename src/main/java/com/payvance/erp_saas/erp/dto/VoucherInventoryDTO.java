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
    private java.math.BigDecimal discount;
    private String hsnCode;
    private String gstTaxability;
    private java.math.BigDecimal gstRate;
    private java.math.BigDecimal igstRate;
    private java.math.BigDecimal cgstRate;
    private java.math.BigDecimal sgstRate;
    private java.math.BigDecimal cessRate;
    private String hsnName;
    private String typeOfSupply;
    private java.math.BigDecimal gstAssblValue;

    // Classification & Tax Amounts
    private String uom;
    private java.math.BigDecimal cgstAmount;
    private java.math.BigDecimal sgstAmount;
    private java.math.BigDecimal igstAmount;

    private java.util.List<VoucherBatchAllocationDTO> batchAllocations;

    private String basicUserDescription;
    private Boolean isDeemedPositive;
}

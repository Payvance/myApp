package com.payvance.erp_saas.erp.dto;

import lombok.Data;

@Data
public class VoucherInventoryDTO {
    private String stockItemGuid;
    private String stockItemName;
    private String actualQty;
    private String billedQty;
    private Double rate;
    private java.math.BigDecimal amount;
    private String hsnCode;
    private String gstTaxability;
    private java.util.List<VoucherBatchAllocationDTO> batchAllocations;
}

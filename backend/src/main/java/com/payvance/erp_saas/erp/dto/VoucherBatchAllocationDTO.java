package com.payvance.erp_saas.erp.dto;

import lombok.Data;

@Data
public class VoucherBatchAllocationDTO {
    private String godownName;
    private String batchName;
    private String actualQty;
    private String billedQty;
    private Double rate;
    private Double amount;
    private Long batchId;
    private String indentNo;
    private String orderNo;
    private String trackingNumber;
    private Double batchDiscount;
}

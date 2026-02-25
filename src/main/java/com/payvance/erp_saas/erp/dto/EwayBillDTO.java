package com.payvance.erp_saas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EwayBillDTO {
    private String billNumber;
    private String billDate;
    private String documentType;
    private String subType;
    private String consignorName;
    private String consignorPlace;
    private String consignorPincode;
    private String consignorAddress;
    private String consigneeName;
    private String consigneePlace;
    private String consigneePincode;
    private String consigneeAddress;
    private String shippedFromState;
    private String shippedToState;
    private String irpSource;
    private String vehicleNumber;
    private String transportMode;
    private String distance;
    private String validUpto;
    private String cancelDate;
    private String cancelReason;
}

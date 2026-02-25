package com.payvance.erp_saas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherEwayBillDTO {
    private String billNumber;
    private LocalDate billDate;
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
    private LocalDate validUpto;
    private LocalDate cancelDate;
    private String cancelReason;
}

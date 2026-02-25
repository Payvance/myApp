package com.payvance.erp_saas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherOrderDetailDTO {
    private String basicPurchaseOrderNo;
    private LocalDate basicOrderDate;
}

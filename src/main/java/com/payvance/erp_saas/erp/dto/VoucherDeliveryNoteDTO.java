package com.payvance.erp_saas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherDeliveryNoteDTO {
    private String number;
    private String date;
}

package com.payvance.erp_saas.erp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class VoucherTileDTO {
    private String name;
    private BigDecimal amount;
    private String type; // DR (Debit) or CR (Credit)
    private boolean hasChildren;
    private List<VoucherTileDTO> children;
}

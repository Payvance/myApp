package com.payvance.erp_saas.erp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DashboardStatsDTO {
    private BigDecimal totalReceivables;
    private BigDecimal totalPayables;
    private BigDecimal cashInHand;
    private BigDecimal bankAccounts;
}

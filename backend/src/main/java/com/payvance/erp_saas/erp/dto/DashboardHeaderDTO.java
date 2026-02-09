package com.payvance.erp_saas.erp.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DashboardHeaderDTO {
    private String companyName;
    private LocalDateTime lastSyncAt;
    private LocalDate fromDate;
    private LocalDate toDate;
    private LocalDate booksFrom;
}

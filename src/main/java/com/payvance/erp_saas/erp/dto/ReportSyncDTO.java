package com.payvance.erp_saas.erp.dto;

import lombok.Data;

@Data
public class ReportSyncDTO {
    private String companyGuid;
    private String reportName;
    private String payload;
}

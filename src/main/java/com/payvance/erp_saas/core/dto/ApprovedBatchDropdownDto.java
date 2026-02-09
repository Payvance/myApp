package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovedBatchDropdownDto {
    private Long id;
    private Long licenseModelId;
    private Integer pendingLicenses; // total - used
    private String displayText; // e.g. "Batch #1 - Plan: Basic (45 left)"
}

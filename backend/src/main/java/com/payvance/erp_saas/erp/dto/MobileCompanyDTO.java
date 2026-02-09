package com.payvance.erp_saas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobileCompanyDTO {
    private Long id;
    private Long tenantId;
    private String name;
    private String guid;
    private String booksFrom;
    private String gstin;
    private String timezone;
    private Integer financialYear;
    private String financialYearFrom;
    private String financialYearTo;

    // License Info (from TallyConfiguration)
    private LocalDate licenseExpiryDate;
    private String licenseSerialNumber;
    private String licenseEmail;

    // Sync Info (from SyncState)
    private LocalDateTime lastSyncTime;
}

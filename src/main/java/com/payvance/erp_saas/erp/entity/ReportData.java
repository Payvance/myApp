package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tally_reports_data")
@Data
public class ReportData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @Column(name = "report_name", nullable = false)
    private String reportName; // e.g., BALANCE_SHEET, PROFIT_AND_LOSS

    @Lob
    @Column(name = "payload", columnDefinition = "LONGTEXT")
    private String payload; // XML or JSON data

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

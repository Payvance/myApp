package com.payvance.erp_saas.core.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * DTO to encapsulate filters for fetching audit logs.
 * Easily extensible for future requirements.
 */
@Getter
@Setter
public class AuditLogFilter {

    private Long tenantId;
    private String actorType;
    private String action;
    private String entityType;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime toDate;
}

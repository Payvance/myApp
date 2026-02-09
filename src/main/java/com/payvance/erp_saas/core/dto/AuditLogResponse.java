package com.payvance.erp_saas.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO used to return audit log info in API responses.
 */
@Getter
@Setter
public class AuditLogResponse {

    private Long id;
    private Long tenantId;
    private String actorType;
    private Long actorUserId;
    private String action;
    private String entityType;
    private Long entityId;
    private String metaJson;
    private LocalDateTime createdAt;
}

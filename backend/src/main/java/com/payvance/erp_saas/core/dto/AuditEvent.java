package com.payvance.erp_saas.core.dto;

import com.payvance.erp_saas.core.enums.RoleEnum;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * DTO representing an audit event.
 */
@Getter
@Builder
public class AuditEvent {

    private Long tenantId;
    private RoleEnum actorType;
    private Long actorUserId;
    private String action;
    private String entityType;
    private Long entityId;
    private Map<String, Object> meta;
}

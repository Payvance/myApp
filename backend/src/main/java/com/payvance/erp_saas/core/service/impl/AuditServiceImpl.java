package com.payvance.erp_saas.core.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payvance.erp_saas.core.dto.AuditEvent;
import com.payvance.erp_saas.core.dto.AuditLogResponse;
import com.payvance.erp_saas.core.dto.AuditLogFilter;
import com.payvance.erp_saas.core.entity.AuditLog;
import com.payvance.erp_saas.core.repository.AuditLogRepository;
import com.payvance.erp_saas.core.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Async
    public void logEvent(AuditEvent event) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setTenantId(event.getTenantId());
            auditLog.setActorType(event.getActorType().name());
            auditLog.setActorUserId(event.getActorUserId());
            auditLog.setAction(event.getAction());
            auditLog.setEntityType(event.getEntityType());
            auditLog.setEntityId(event.getEntityId());

            if (event.getMeta() != null) {
                auditLog.setMetaJson(toJson(event.getMeta()));
            }

            auditLogRepository.save(auditLog);

        } catch (Exception ex) {
            log.error("Failed to save audit log: {}", ex.getMessage(), ex);
        }
    }

    private String toJson(Object meta) throws JsonProcessingException {
        return objectMapper.writeValueAsString(meta);
    }

    @Override
    public Page<AuditLogResponse> getAuditLogs(AuditLogFilter filter, Pageable pageable) {
        Page<AuditLog> logs = auditLogRepository.findAuditLogs(
                filter.getTenantId(),
                filter.getActorType(),
                filter.getAction(),
                filter.getEntityType(),
                filter.getFromDate(),
                filter.getToDate(),
                pageable
        );

        List<AuditLogResponse> responses = logs.stream().map(this::mapToDto).collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, logs.getTotalElements());
    }

    @Override
    public List<AuditLogResponse> getAuditLogsCsv(AuditLogFilter filter) {
        List<AuditLog> logs = auditLogRepository.findAuditLogs(
                filter.getTenantId(),
                filter.getActorType(),
                filter.getAction(),
                filter.getEntityType(),
                filter.getFromDate(),
                filter.getToDate(),
                Pageable.unpaged()
        ).getContent();

        return logs.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private AuditLogResponse mapToDto(AuditLog a) {
        AuditLogResponse r = new AuditLogResponse();
        r.setId(a.getId());
        r.setTenantId(a.getTenantId());
        r.setActorType(a.getActorType());
        r.setActorUserId(a.getActorUserId());
        r.setAction(a.getAction());
        r.setEntityType(a.getEntityType());
        r.setEntityId(a.getEntityId());
        r.setMetaJson(a.getMetaJson());
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }
    // Get a single audit log by its ID
    @Override
    public AuditLogResponse getAuditLogById(Long id) {
        AuditLog log = auditLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit log not found"));

        return mapToDto(log);
    }

}

package com.payvance.erp_saas.core.controller;

import com.payvance.erp_saas.core.dto.AuditEvent;
import com.payvance.erp_saas.core.dto.AuditLogFilter;
import com.payvance.erp_saas.core.dto.AuditLogResponse;
import com.payvance.erp_saas.core.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for Audit Logs.
 * Supports filtered fetch and CSV download.
 */
@RestController
@RequestMapping("/api/audit/logs")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @PostMapping
    public ResponseEntity<String> createAudit(@RequestBody AuditEvent event) {
        auditService.logEvent(event);
        return ResponseEntity.ok("Audit log created");
    }

    @GetMapping
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(AuditLogFilter filter, Pageable pageable) {
        return ResponseEntity.ok(auditService.getAuditLogs(filter, pageable));
    }

    @GetMapping("/download")
    public ResponseEntity<String> downloadAuditLogsCsv(AuditLogFilter filter) {
        List<AuditLogResponse> logs = auditService.getAuditLogsCsv(filter);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,TenantId,ActorType,ActorUserId,Action,EntityType,EntityId,MetaJson,CreatedAt\n");

        for (AuditLogResponse log : logs) {
            csv.append(log.getId()).append(",")
                    .append(log.getTenantId()).append(",")
                    .append(log.getActorType()).append(",")
                    .append(log.getActorUserId()).append(",")
                    .append(log.getAction()).append(",")
                    .append(log.getEntityType()).append(",")
                    .append(log.getEntityId()).append(",")
                    .append("\"").append(log.getMetaJson() != null ? log.getMetaJson().replace("\"", "\"\"") : "").append("\",")
                    .append(log.getCreatedAt()).append("\n");
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=audit_logs.csv")
                .header("Content-Type", "text/csv")
                .body(csv.toString());
    }

            @GetMapping("/{id}")
        public ResponseEntity<AuditLogResponse> getAuditLogById(@PathVariable Long id) {
            return ResponseEntity.ok(auditService.getAuditLogById(id));
        }

}

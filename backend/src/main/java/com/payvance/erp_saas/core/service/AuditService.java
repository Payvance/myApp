package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.dto.AuditEvent;
import com.payvance.erp_saas.core.dto.AuditLogResponse;
import com.payvance.erp_saas.core.dto.AuditLogFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuditService {

    void logEvent(AuditEvent event);

    // Fetch paginated logs with filters
    Page<AuditLogResponse> getAuditLogs(AuditLogFilter filter, Pageable pageable);

    // Fetch all logs with filters (for CSV export)
    List<AuditLogResponse> getAuditLogsCsv(AuditLogFilter filter);

    // Fetch a single audit log by its ID
    AuditLogResponse getAuditLogById(Long id);

}

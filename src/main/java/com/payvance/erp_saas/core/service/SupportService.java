package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.dto.SupportNotifyRequest;
import com.payvance.erp_saas.core.entity.Tenant;
import com.payvance.erp_saas.core.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final TenantRepository tenantRepository;
    private final EmailService emailService;

    public void notifySupportUser(Long tenantId, SupportNotifyRequest request) {
        // 1️⃣ Fetch tenant
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        // 2️⃣ Prepare template variables
        Map<String, Object> vars = new HashMap<>();
        vars.put("subject", "Support Ticket Created");
        vars.put("title", "Support Request Received");
        vars.put("name", tenant.getName());
        vars.put("message",
                "Your support ticket has been created successfully.\n\n" +
                "Ticket ID: #" + request.getTicket_id() + "\n" +
                "Category: " + request.getCategory() + "\n\n" +
                request.getMessage()
        );
        vars.put("status", "submitted");

        // 3️⃣ Send mail using universal template
        emailService.SupportSendUniversalEmail(
                tenant.getEmail(),
                "Support Ticket #" + request.getTicket_id(),
                vars,
                null
        );
    }
}

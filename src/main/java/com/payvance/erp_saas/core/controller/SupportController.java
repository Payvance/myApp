package com.payvance.erp_saas.core.controller;

import com.payvance.erp_saas.core.dto.SupportNotifyRequest;
import com.payvance.erp_saas.core.entity.Tenant;
import com.payvance.erp_saas.core.repository.TenantRepository;
import com.payvance.erp_saas.core.service.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final TenantRepository tenantRepository;
    private final EmailService emailService;

    @PostMapping("/notify-user")
    public ResponseEntity<?> notifyUser(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @RequestBody SupportNotifyRequest request
    ) {

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

        // 3️⃣ Send mail using EXISTING universal template
        emailService.SupportSendUniversalEmail(
                tenant.getEmail(),
                "Support Ticket #" + request.getTicket_id(),
                vars,
                null
        );

        // 4️⃣ Response
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Mail sent to tenant"
        ));
    }
}

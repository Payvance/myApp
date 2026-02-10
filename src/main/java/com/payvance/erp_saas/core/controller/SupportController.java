package com.payvance.erp_saas.core.controller;

import com.payvance.erp_saas.core.dto.SupportNotifyRequest;
import com.payvance.erp_saas.core.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

 private final SupportService supportService;

     @PostMapping("/notify-user")
    public ResponseEntity<?> notifyUser(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @RequestBody SupportNotifyRequest request
    ) {
        supportService.notifySupportUser(tenantId, request);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Mail sent to tenant"
        ));
    }
}

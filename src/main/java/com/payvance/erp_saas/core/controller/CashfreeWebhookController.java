package com.payvance.erp_saas.core.controller;

import com.payvance.erp_saas.core.service.CashfreeWebhookService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/billing/cashfree/webhook")
@RequiredArgsConstructor
public class CashfreeWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(CashfreeWebhookController.class);
    private final CashfreeWebhookService cashfreeWebhookService;

    @PostMapping
    public ResponseEntity<String> handleCashfreeWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "x-webhook-signature", required = false) String signature,
            @RequestHeader(value = "x-webhook-timestamp", required = false) String timestamp) {
        
        logger.info("Received Cashfree Webhook: {}", payload);
        
        try {
            cashfreeWebhookService.handleWebhook(payload, signature, timestamp);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            logger.error("Error processing Cashfree Webhook: {}", e.getMessage());
            return ResponseEntity.ok("OK");
        }
    }
}

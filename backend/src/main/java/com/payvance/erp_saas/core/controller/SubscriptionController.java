package com.payvance.erp_saas.core.controller;

import com.payvance.erp_saas.core.entity.Invoice;
import com.payvance.erp_saas.core.entity.Payment;
import com.payvance.erp_saas.core.service.PaymentService;
import com.payvance.erp_saas.core.service.RazorpayWebhookService;
import com.payvance.erp_saas.core.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    // private final PaymentService paymentService;
    // private final RazorpayWebhookService webhookService;

    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, Object>> startSubscription(@RequestBody Map<String, Object> request) {
        Long tenantId = Long.valueOf(request.get("tenant_id").toString());
        Long planId = Long.valueOf(request.get("plan_id").toString());
        List<Map<String, Object>> addons = (List<Map<String, Object>>) request.get("addons");
        
        // Get discount if provided
        BigDecimal discount = null;
        if (request.containsKey("discount") && request.get("discount") != null) {
            discount = new BigDecimal(request.get("discount").toString());
        }
        
        Invoice invoice = subscriptionService.startSubscription(tenantId, planId, addons, discount);
        
        // Commented out until payment gateway is implemented
        // Payment payment = paymentService.createRazorpayOrder(invoice.getId());
        
        return ResponseEntity.ok(Map.of(
            "invoice_id", invoice.getId(),
            "invoice_number", invoice.getInvoiceNumber(),
            "amount", invoice.getTotalPayable(),
            "message", "Invoice generated and sent to registered email for offline payment."
            // "order_id", payment.getGatewayOrderId()
        ));
    }

    @PostMapping("/renew")
    public ResponseEntity<Map<String, Object>> renew(@RequestBody Map<String, Object> request) {
        Long tenantId = Long.valueOf(request.get("tenant_id").toString());
        Invoice invoice = subscriptionService.renewSubscription(tenantId);
        
        // Commented out until payment gateway is implemented
        // Payment payment = paymentService.createRazorpayOrder(invoice.getId());
        
        return ResponseEntity.ok(Map.of(
            "invoice_id", invoice.getId(),
            "invoice_number", invoice.getInvoiceNumber(),
            "amount", invoice.getTotalPayable(),
            "message", "Renewal invoice generated and sent to registered email."
            // "order_id", payment.getGatewayOrderId()
        ));
    }

    @PostMapping("/upgrade")
    public ResponseEntity<Map<String, Object>> upgrade(@RequestBody Map<String, Object> request) {
        Long tenantId = Long.valueOf(request.get("tenant_id").toString());
        Long toPlanId = Long.valueOf(request.get("to_plan_id").toString());
        Long toPlanPriceId = Long.valueOf(request.get("to_plan_price_id").toString());
        
        Invoice invoice = subscriptionService.startUpgrade(tenantId, toPlanId, toPlanPriceId);
        
        // Commented out until payment gateway is implemented
        // Payment payment = paymentService.createRazorpayOrder(invoice.getId());
        
        return ResponseEntity.ok(Map.of(
            "invoice_id", invoice.getId(),
            "invoice_number", invoice.getInvoiceNumber(),
            "amount", invoice.getTotalPayable(),
            "message", "Upgrade invoice generated and sent to registered email."
            // "order_id", payment.getGatewayOrderId()
        ));
    }

    @PostMapping("/webhook/razorpay")
    public ResponseEntity<String> razorpayWebhook(@RequestBody Map<String, Object> payload, 
                                                @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        // Commented out until payment gateway is implemented
        // webhookService.handleWebhook(payload, signature);
        return ResponseEntity.ok("Webhook received (Gateway integration inactive)");
    }

    // ===================================================================
    // TEMPORARY: Payment Simulation Endpoint (Remove when gateway is ready)
    // ===================================================================
    
    /**
     * Simulates successful payment for testing the complete flow.
     * Call this after creating a subscription to test activation.
     * 
     * Example: POST /billing/simulate-payment
     * Body: { "invoice_id": 123 }
     */
    @PostMapping("/simulate-payment")
    public ResponseEntity<Map<String, Object>> simulatePayment(@RequestBody Map<String, Object> request) {
        Long invoiceId = Long.valueOf(request.get("invoice_id").toString());
        Map<String, Object> result = subscriptionService.simulatePaymentSuccess(invoiceId);
        return ResponseEntity.ok(result);
    }
}

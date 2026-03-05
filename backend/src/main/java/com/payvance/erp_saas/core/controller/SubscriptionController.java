package com.payvance.erp_saas.core.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.entity.Invoice;
import com.payvance.erp_saas.core.entity.Payment;
import com.payvance.erp_saas.core.service.CashfreeWebhookService;
import com.payvance.erp_saas.core.service.PaymentService;
import com.payvance.erp_saas.core.service.SubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;
    private final CashfreeWebhookService cashfreeWebhookService;

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
        
        Invoice invoice = subscriptionService.startSubscription(tenantId, planId, addons, discount, null);
        
        Payment payment = paymentService.createCashfreeOrder(invoice.getId());
        
        return ResponseEntity.ok(Map.of(
            "invoice_id", invoice.getId(),
            "invoice_number", invoice.getInvoiceNumber(),
            "amount", invoice.getTotalPayable(),
            "cf_order_id", payment.getGatewayOrderId(),
            "message", "Order created. Please complete payment via Cashfree."
        ));
    }

    /**
     * Unified endpoint to process subscriptions (Dispatcher).
     * Automatically handles first-time payment, upgrade, renewal, or direct payment.
     */
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processSubscription(@RequestBody Map<String, Object> request) {
        Long tenantId = Long.valueOf(request.get("tenant_id").toString());
        Long planId = Long.valueOf(request.get("plan_id").toString());
        List<Map<String, Object>> addons = (List<Map<String, Object>>) request.get("addons");
        String couponCode = (String) request.get("coupon_code");
        String referralCode = (String) request.get("referral_code");
        
        BigDecimal walletAmount = BigDecimal.ZERO;
        if (request.containsKey("wallet_amount") && request.get("wallet_amount") != null) {
            walletAmount = new BigDecimal(request.get("wallet_amount").toString());
        }

        // 1. Business Logic: Create Subscription & Invoice
        Invoice invoice = subscriptionService.processSubscription(
                tenantId, planId, addons, couponCode, referralCode, walletAmount);

        // 2. Orchestration: Create Payment Order
        Payment payment = paymentService.createCashfreeOrder(invoice.getId());

        return ResponseEntity.ok(buildPaymentResponse(invoice, payment));
    }

    @PostMapping("/renew")
    public ResponseEntity<Map<String, Object>> renew(@RequestBody Map<String, Object> request) {
        Long tenantId = Long.valueOf(request.get("tenant_id").toString());
        Invoice invoice = subscriptionService.renewSubscription(tenantId);
        Payment payment = paymentService.createCashfreeOrder(invoice.getId());
        return ResponseEntity.ok(buildPaymentResponse(invoice, payment));
    }

    @PostMapping("/upgrade")
    public ResponseEntity<Map<String, Object>> upgrade(@RequestBody Map<String, Object> request) {
        Long tenantId = Long.valueOf(request.get("tenant_id").toString());
        Long toPlanId = Long.valueOf(request.get("to_plan_id").toString());
        Long toPlanPriceId = Long.valueOf(request.get("to_plan_price_id").toString());
        Invoice invoice = subscriptionService.startUpgrade(tenantId, toPlanId, toPlanPriceId);
        Payment payment = paymentService.createCashfreeOrder(invoice.getId());
        return ResponseEntity.ok(buildPaymentResponse(invoice, payment));
    }

    private Map<String, Object> buildPaymentResponse(Invoice invoice, Payment payment) {
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("invoice_id", invoice.getId());
        response.put("invoice_number", invoice.getInvoiceNumber());
        response.put("amount", invoice.getTotalPayable());
        response.put("cf_order_id", payment.getGatewayOrderId());
        response.put("payment_session_id", payment.getPaymentSessionId());
        response.put("message", "Order created. Open Cashfree payment using payment_session_id.");
        return response;
    }

    /**
     * Simulates successful payment for testing the complete flow.
     */
    @PostMapping("/simulate-payment")
    public ResponseEntity<Map<String, Object>> simulatePayment(@RequestBody Map<String, Object> request) {
        Long invoiceId = Long.valueOf(request.get("invoice_id").toString());
        
        // Orchestrate Payment Completion
        paymentService.markInvoicePaid(invoiceId, "SIM_MOCK_PAY_ID_" + System.currentTimeMillis());
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Payment simulated. Activation triggers executed.",
            "invoice_id", invoiceId
        ));
    }
}

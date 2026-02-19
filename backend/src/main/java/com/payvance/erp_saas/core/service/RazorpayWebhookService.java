package com.payvance.erp_saas.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payvance.erp_saas.core.entity.Payment;
import com.payvance.erp_saas.core.entity.PaymentWebhook;
import com.payvance.erp_saas.core.repository.PaymentRepository;
import com.payvance.erp_saas.core.repository.PaymentWebhookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RazorpayWebhookService {

    private final PaymentWebhookRepository webhookRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @Transactional
    public void handleWebhook(Map<String, Object> payload, String signature) {
        // 1. Audit log raw webhook
        PaymentWebhook webhook = new PaymentWebhook();
        webhook.setGateway("razorpay");  //for now hardcoding, can be dynamic based on request or config
        webhook.setEventType(payload.get("event").toString());
        
        try {
            webhook.setPayloadJson(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            webhook.setPayloadJson(payload.toString());
        }
        
        webhook.setStatus("received");
        
        // 2. Signature verification (Mocked for now - requires secret)
        boolean isValid = verifySignature(payload, signature);
        webhook.setSignatureValid(isValid);
        
        if (!isValid) {
            webhook.setStatus("ignored");
            webhookRepository.save(webhook);
            return;
        }

        webhookRepository.save(webhook);

        // 3. Process Event
        String event = payload.get("event").toString();
        if ("payment.captured".equals(event) || "order.paid".equals(event)) {
            processPaymentSuccess(payload, webhook);
        }
    }

    private boolean verifySignature(Map<String, Object> payload, String signature) {
        // Implement HmacSHA256 verification here
        return true; 
    }

    private void processPaymentSuccess(Map<String, Object> payload, PaymentWebhook webhook) {
        Map<String, Object> paymentEntity = (Map<String, Object>) ((Map<String, Object>) payload.get("payload")).get("payment");
        Map<String, Object> entity = (Map<String, Object>) paymentEntity.get("entity");
        
        String orderId = entity.get("order_id").toString();
        String paymentId = entity.get("id").toString();

        Payment payment = paymentRepository.findByGatewayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment record not found for order: " + orderId));

        webhook.setTenantId(payment.getTenantId());
        webhook.setInvoiceId(payment.getInvoiceId());
        webhook.setStatus("processed");
        webhookRepository.save(webhook);

        paymentService.markInvoicePaid(payment.getInvoiceId(), paymentId);
    }
}

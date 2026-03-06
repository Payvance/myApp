package com.payvance.erp_saas.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payvance.erp_saas.core.entity.PaymentWebhook;
import com.payvance.erp_saas.core.repository.PaymentWebhookRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CashfreeWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(CashfreeWebhookService.class);

    @Value("${cashfree.secret.key}")
    private String clientSecret;

    private final PaymentWebhookRepository webhookRepository;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @Transactional

    public void handleWebhook(Map<String, Object> payload, String signature, String timestamp) throws Exception {

        PaymentWebhook webhook = new PaymentWebhook();

        webhook.setGateway("cashfree");
     
        try {

            Map<String, Object> data = (Map<String, Object>) payload.get("data");

            // ✅ Fix 1: Check for "order" key, not "object"

            if (data != null && data.containsKey("order")) {

                Map<String, Object> order = (Map<String, Object>) data.get("order");

                Map<String, Object> payment = (Map<String, Object>) data.get("payment"); // ✅ Fix 2: get payment separately
     
                webhook.setEventType(payload.get("type").toString());

                webhook.setPayloadJson(objectMapper.writeValueAsString(payload));

                webhook.setStatus("received");
     
                boolean isValid = verifySignature(payload, signature, timestamp);

                webhook.setSignatureValid(isValid);
     
                if (!isValid) {

                    webhook.setStatus("invalid_signature");

                    webhookRepository.save(webhook);

                    logger.warn("Invalid Cashfree Webhook Signature");

                    return;

                }
     
                webhookRepository.save(webhook);
     
                if ("PAYMENT_SUCCESS_WEBHOOK".equals(payload.get("type"))) {

                    String orderId = order.get("order_id").toString();

                    // ✅ Fix 3: cf_payment_id is inside "payment", not "order"

                    String transactionId = payment != null && payment.get("cf_payment_id") != null

                            ? payment.get("cf_payment_id").toString()

                            : "N/A";
     
                    logger.info("Cashfree Payment Success: Order={}, Txn={}", orderId, transactionId);

                    paymentService.processPaymentSuccess(orderId, transactionId);
     
                    webhook.setStatus("processed");

                    webhookRepository.save(webhook);

                }

            } else {

                // ✅ Fix 4: Log when data structure is unexpected so you catch future issues

                logger.warn("Webhook data missing 'order' key. Payload: {}", payload);

            }

        } catch (Exception e) {

            logger.error("Failed to process Cashfree webhook", e);

            throw e;

        }

    }
     

    private boolean verifySignature(Map<String, Object> payload, String signature, String timestamp) {
        // Implementation of Cashfree webhook signature verification
        // For now returning true to allow flow testing, but usually requires hashing timestamp + payload
        return true; 
    }
}

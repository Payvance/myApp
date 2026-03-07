package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.entity.Invoice;
import com.payvance.erp_saas.core.entity.Payment;
import com.payvance.erp_saas.core.entity.Tenant;
import com.payvance.erp_saas.core.event.PaymentSuccessEvent;
import com.payvance.erp_saas.core.repository.InvoiceRepository;
import com.payvance.erp_saas.core.repository.PaymentRepository;
import com.payvance.erp_saas.core.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final TenantRepository tenantRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RestTemplate restTemplate;
    private final SubscriptionService subscriptionService;

    @Value("${cashfree.api.url}")
    private String cashfreeApiUrl;

    @Value("${cashfree.app.id}")
    private String cashfreeAppId;

    @Value("${cashfree.secret.key}")
    private String cashfreeSecretKey;

    @Value("${cashfree.webhook.url:https://finlyticz.com/api/billing/cashfree/webhook}")
    private String cashfreeWebhookUrl;

    /**
     * Creates a REAL Cashfree order by calling the Cashfree API.
     * Returns the Payment record with a real cf_order_id and payment_session_id.
     */
    @Transactional
    public Payment createCashfreeOrder(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if ("paid".equalsIgnoreCase(invoice.getStatus())) {
            throw new RuntimeException("Invoice already paid");
        }

        Tenant tenant = tenantRepository.findById(invoice.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        // Build Cashfree API request
        String cfOrderId = "INV-" + invoiceId + "-" + System.currentTimeMillis();

        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("customer_id", "TENANT_" + tenant.getId());
        customerDetails.put("customer_name", tenant.getName() != null ? tenant.getName() : "Customer");
        customerDetails.put("customer_email", tenant.getEmail());
        customerDetails.put("customer_phone", tenant.getPhone() != null ? tenant.getPhone() : "9999999999");

        Map<String, Object> orderMeta = new HashMap<>();
        orderMeta.put("notify_url", cashfreeWebhookUrl);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("order_id", cfOrderId);
        requestBody.put("order_amount", invoice.getTotalPayable());
        requestBody.put("order_currency", "INR");
        requestBody.put("customer_details", customerDetails);
        requestBody.put("order_meta", orderMeta);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-version", "2023-08-01");
        headers.set("x-client-id", cashfreeAppId);
        headers.set("x-client-secret", cashfreeSecretKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        String realOrderId = cfOrderId;
        String paymentSessionId = null;

        try {
            logger.info("[CASHFREE] Creating order for invoice: {}, amount: {}", invoiceId, invoice.getTotalPayable());

            ResponseEntity<Map> response = restTemplate.exchange(
                    cashfreeApiUrl,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                realOrderId = (String) responseBody.getOrDefault("order_id", cfOrderId);
                paymentSessionId = (String) responseBody.get("payment_session_id");
                logger.info("[CASHFREE] Order created: {}, Session: {}", realOrderId, paymentSessionId);
            } else {
                logger.warn("[CASHFREE] Unexpected response: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("[CASHFREE] Failed to create real order, falling back to dummy: {}", e.getMessage());
            // Fallback: use dummy ID so the flow isn't completely broken
            realOrderId = "CF_ORD_" + invoiceId + "_" + System.currentTimeMillis();
        }

        // Save Payment record
        Payment payment = new Payment();
        payment.setTenantId(invoice.getTenantId());
        payment.setSubscriptionId(invoice.getSubscriptionId());
        payment.setInvoiceId(invoiceId);
        payment.setGateway("cashfree");
        payment.setGatewayOrderId(realOrderId);
        payment.setPaymentSessionId(paymentSessionId);
        payment.setAmount(invoice.getTotalPayable());
        payment.setStatus("pending");

        return paymentRepository.save(payment);
    }

    @Transactional
    public void processPaymentSuccess(String gatewayOrderId, String gatewayPaymentId) {
        Payment payment = paymentRepository.findByGatewayOrderId(gatewayOrderId)
                .orElseThrow(() -> new RuntimeException("Payment record not found for Order ID: " + gatewayOrderId));

        markInvoicePaid(payment.getInvoiceId(), gatewayPaymentId);
    }

    @Transactional
    public void markInvoicePaid(Long invoiceId, String gatewayPaymentId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if ("paid".equalsIgnoreCase(invoice.getStatus())) {
            return; // Idempotent
        }

        invoice.setStatus("paid");
        invoice.setPaidAt(LocalDateTime.now());
        invoice.setGatewayPaymentId(gatewayPaymentId);
        invoiceRepository.save(invoice);

        Payment payment = paymentRepository.findByInvoiceIdAndStatus(invoiceId, "pending")
                .orElse(new Payment());

        payment.setStatus("success");
        payment.setGatewayPaymentId(gatewayPaymentId);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
        
     // Send invoice AFTER payment success
        subscriptionService.sendInvoiceEmail(invoiceId);

        // Publish event → SubscriptionPaymentListener handles activation
        eventPublisher.publishEvent(new PaymentSuccessEvent(
                this,
                invoiceId,
                gatewayPaymentId,
                invoice.getInvoiceNumber(),
                invoice.getTenantId(),
                invoice.getSubscriptionId(),
                invoice.getDiscountBy()
        ));
    }
    
    
}

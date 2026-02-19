package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.entity.Invoice;
import com.payvance.erp_saas.core.entity.Payment;
import com.payvance.erp_saas.core.repository.InvoiceRepository;
import com.payvance.erp_saas.core.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final SubscriptionService subscriptionService;

    @Transactional
    public Payment createRazorpayOrder(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if ("paid".equalsIgnoreCase(invoice.getStatus())) {
            throw new RuntimeException("Invoice already paid");
        }

        // Razorpay API call would go here to get rp_order_id
        String rpOrderId = "order_mock_" + System.currentTimeMillis(); 

        Payment payment = new Payment();
        payment.setTenantId(invoice.getTenantId());
        payment.setSubscriptionId(invoice.getSubscriptionId());
        payment.setInvoiceId(invoiceId);
        payment.setGateway("razorpay");   //for now hardcoding, can be dynamic based on request or config
        payment.setGatewayOrderId(rpOrderId);
        payment.setAmount(invoice.getTotalPayable());
        payment.setStatus("pending");
        
        return paymentRepository.save(payment);
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
                .orElse(new Payment()); // Fallback if no pending payment found
        
        payment.setStatus("success");
        payment.setGatewayPaymentId(gatewayPaymentId);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // Chain to activation or upgrade/renewal logic
        // Identify flow from invoice number or metadata
        if (invoice.getInvoiceNumber().startsWith("INV-")) {
            subscriptionService.activateTenantAfterPaid(invoice.getTenantId(), invoice.getSubscriptionId());
        } else if (invoice.getInvoiceNumber().startsWith("REN-")) {
            subscriptionService.applyRenewal(invoiceId);
        } else if (invoice.getInvoiceNumber().startsWith("UPG-")) {
            subscriptionService.applyUpgrade(invoiceId);
        }
    }
}

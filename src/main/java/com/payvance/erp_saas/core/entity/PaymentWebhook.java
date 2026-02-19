package com.payvance.erp_saas.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_webhooks")
@Getter
@Setter
public class PaymentWebhook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "gateway", length = 50)
    private String gateway;

    @Column(name = "event_type", length = 100)
    private String eventType;

    @Column(name = "payload_json", columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "signature_valid")
    private boolean signatureValid = false;

    @Column(name = "status", length = 30)
    private String status = "received";

    @CreationTimestamp
    @Column(name = "received_at", updatable = false)
    private LocalDateTime receivedAt;
}

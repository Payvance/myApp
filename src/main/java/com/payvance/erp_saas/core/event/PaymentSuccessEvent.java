package com.payvance.erp_saas.core.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentSuccessEvent extends ApplicationEvent {
    private final Long invoiceId;
    private final String gatewayPaymentId;
    private final String invoiceNumber;
    private final Long tenantId;
    private final Long subscriptionId;
    private final String discountBy;

    public PaymentSuccessEvent(Object source, Long invoiceId, String gatewayPaymentId, 
                               String invoiceNumber, Long tenantId, Long subscriptionId, String discountBy) {
        super(source);
        this.invoiceId = invoiceId;
        this.gatewayPaymentId = gatewayPaymentId;
        this.invoiceNumber = invoiceNumber;
        this.tenantId = tenantId;
        this.subscriptionId = subscriptionId;
        this.discountBy = discountBy;
    }
}

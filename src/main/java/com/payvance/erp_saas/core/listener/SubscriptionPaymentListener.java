package com.payvance.erp_saas.core.listener;

import com.payvance.erp_saas.core.event.PaymentSuccessEvent;
import com.payvance.erp_saas.core.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class SubscriptionPaymentListener {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionPaymentListener.class);
    private final SubscriptionService subscriptionService;

    @EventListener
    @Transactional
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        logger.info("[LISTENER] Handling PaymentSuccessEvent for Invoice: {}", event.getInvoiceNumber());

        // 1. Process Wallet Usage & Referrals (non-critical — failures should NOT block activation)
        if (event.getDiscountBy() != null && !event.getDiscountBy().isEmpty()) {
            String[] parts = event.getDiscountBy().split("\\|");
            for (String part : parts) {
                if (part.startsWith("wallet_usage:")) {
                    try {
                        String walletAmtStr = part.split(":")[1];
                        BigDecimal walletAmt = new BigDecimal(walletAmtStr);
                        subscriptionService.deductWalletBalance(event.getTenantId(), walletAmt, "INVOICE", event.getInvoiceId());
                        logger.info("[LISTENER] Wallet deducted: {} for tenant: {}", walletAmt, event.getTenantId());
                    } catch (Exception e) {
                        logger.error("[LISTENER] Failed to deduct wallet balance: {}. Continuing activation.", e.getMessage());
                    }
                } else if (part.startsWith("referral_code:")) {
                    try {
                        String refCode = part.split(":")[1];
                        subscriptionService.handleReferral(event.getTenantId(), refCode);
                        logger.info("[LISTENER] Referral processed for code: {}", refCode);
                    } catch (Exception e) {
                        logger.error("[LISTENER] Failed to process referral: {}. Continuing activation.", e.getMessage());
                    }
                }
            }
        }

        // 2. Critical: Activate tenant / apply renewal / apply upgrade
        // Each step is wrapped independently so one failure doesn't break another
        try {
            String invoiceNumber = event.getInvoiceNumber();

            if (invoiceNumber != null && invoiceNumber.startsWith("INV-")) {
                logger.info("[LISTENER] Activating tenant: {} for invoice: {}", event.getTenantId(), invoiceNumber);
                subscriptionService.activateTenantAfterPaid(event.getTenantId(), event.getSubscriptionId());
                logger.info("[LISTENER] Tenant {} activated successfully.", event.getTenantId());

            } else if (invoiceNumber != null && invoiceNumber.startsWith("REN-")) {
                logger.info("[LISTENER] Applying renewal for invoice: {}", invoiceNumber);
                subscriptionService.applyRenewal(event.getInvoiceId());
                logger.info("[LISTENER] Renewal applied for invoice: {}", event.getInvoiceId());

            } else if (invoiceNumber != null && invoiceNumber.startsWith("UPG-")) {
                logger.info("[LISTENER] Applying upgrade for invoice: {}", invoiceNumber);
                subscriptionService.applyUpgrade(event.getInvoiceId());
                logger.info("[LISTENER] Upgrade applied for invoice: {}", event.getInvoiceId());

            } else {
                logger.warn("[LISTENER] Unrecognized invoice prefix: {}. No activation triggered.", invoiceNumber);
            }

        } catch (Exception e) {
            logger.error("[LISTENER] CRITICAL: Activation failed for tenant {} / invoice {}: {}",
                    event.getTenantId(), event.getInvoiceId(), e.getMessage(), e);
            throw e; // Re-throw so transaction rolls back and issue is visible
        }
    }
}

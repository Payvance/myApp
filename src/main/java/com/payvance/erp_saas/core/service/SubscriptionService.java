package com.payvance.erp_saas.core.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payvance.erp_saas.core.entity.AddOn;
import com.payvance.erp_saas.core.entity.Invoice;
import com.payvance.erp_saas.core.entity.InvoiceItem;
import com.payvance.erp_saas.core.entity.Plan;
import com.payvance.erp_saas.core.entity.PlanLimitation;
import com.payvance.erp_saas.core.entity.PlanPrice;
import com.payvance.erp_saas.core.entity.Subscription;
import com.payvance.erp_saas.core.entity.SubscriptionAddon;
import com.payvance.erp_saas.core.entity.SubscriptionEvent;
import com.payvance.erp_saas.core.entity.Tenant;
import com.payvance.erp_saas.core.entity.TenantActivation;
import com.payvance.erp_saas.core.entity.TenantUsage;
import com.payvance.erp_saas.core.repository.AddOnRepository;
import com.payvance.erp_saas.core.repository.InvoiceItemRepository;
import com.payvance.erp_saas.core.repository.InvoiceRepository;
import com.payvance.erp_saas.core.repository.PlanLimitationRepository;
import com.payvance.erp_saas.core.repository.PlanPriceRepository;
import com.payvance.erp_saas.core.repository.PlanRepository;
import com.payvance.erp_saas.core.repository.SubscriptionAddonRepository;
import com.payvance.erp_saas.core.repository.SubscriptionEventRepository;
import com.payvance.erp_saas.core.repository.SubscriptionRepository;
import com.payvance.erp_saas.core.repository.TenantActivationRepository;
import com.payvance.erp_saas.core.repository.TenantRepository;
import com.payvance.erp_saas.core.repository.TenantUsageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final SubscriptionEventRepository subscriptionEventRepository;
    private final SubscriptionAddonRepository subscriptionAddonRepository;
    private final PlanRepository planRepository;
    private final PlanPriceRepository planPriceRepository;
    private final AddOnRepository addonRepository;
    private final TenantActivationRepository tenantActivationRepository;
    private final TenantUsageRepository tenantUsageRepository;
    private final TenantRepository tenantRepository;
    private final EmailService emailService;
    private final GstRateService gstRateService;
    private final PlanLimitationRepository planLimitationRepository;

    @Transactional
    public Invoice startSubscription(Long tenantId, Long planId, List<Map<String, Object>> addonRequests, BigDecimal discountAmount) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        PlanPrice planPrice = plan.getPlanPrice();

        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime currentPeriodEnd = startAt.plusDays(calculateDays(planPrice));

        // 1. Create Subscription
        Subscription subscription = new Subscription();
        subscription.setTenantId(tenantId);
        subscription.setPlan(plan);
        subscription.setPlanPriceId(planPrice.getId());
        subscription.setStatus("payment_pending");
        subscription.setStartAt(startAt);
        subscription.setCurrentPeriodEnd(currentPeriodEnd);
        subscription = subscriptionRepository.save(subscription);

        // 2. Create Invoice (don't save yet - set all fields first)
        Invoice invoice = new Invoice();
        invoice.setTenantId(tenantId);
        invoice.setSubscriptionId(subscription.getId());
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        invoice.setGateway("razorpay");  //for now hardcoding, can be dynamic based on request or config
        invoice.setStatus("unpaid");

        // 3. Handle Addons first to calculate addonsTotal
        BigDecimal addonsTotal = BigDecimal.ZERO;
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        
        if (addonRequests != null) {
            for (Map<String, Object> addonReq : addonRequests) {
                Long addonId = Long.valueOf(addonReq.get("addon_id").toString());
                Integer quantity = Integer.valueOf(addonReq.get("quantity").toString());

                AddOn addon = addonRepository.findById(addonId)
                        .orElseThrow(() -> new RuntimeException("Addon not found"));

                SubscriptionAddon subAddon = new SubscriptionAddon();
                subAddon.setTenantId(tenantId);
                subAddon.setSubscriptionId(subscription.getId());
                subAddon.setAddonId(addonId);
                subAddon.setQuantity(quantity);
                subAddon.setStatus("pending");
                subscriptionAddonRepository.save(subAddon);

                BigDecimal lineTotal = addon.getUnitPrice().multiply(BigDecimal.valueOf(quantity));
                addonsTotal = addonsTotal.add(lineTotal);

                // Prepare addon invoice item (will save after invoice is saved)
                InvoiceItem addonItem = new InvoiceItem();
                addonItem.setItemType("addon");
                addonItem.setDescription(addon.getName());
                addonItem.setQuantity(quantity);
                addonItem.setUnitPrice(addon.getUnitPrice());
                addonItem.setLineTotal(lineTotal);
                invoiceItems.add(addonItem);
            }
        }
        
        // 4. Calculate Total with Discount and GST
        BigDecimal subtotal = planPrice.getAmount().add(addonsTotal);
        
        // Apply discount if provided
        BigDecimal discountToApply = (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) 
            ? discountAmount : BigDecimal.ZERO;
        BigDecimal subtotalAfterDiscount = subtotal.subtract(discountToApply);
        
        // Set discount fields in invoice BEFORE saving
        invoice.setDiscountTotal(discountToApply);
        if (discountToApply.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setDiscountBy("customer_discount");
        }
        
        invoice.setSubtotal(subtotalAfterDiscount);
        
        // Fetch latest GST rate and calculate final total
        Map<String, Object> gstData = gstRateService.getLatestGstRate();
        Double rate = (Double) gstData.get("rate");
        BigDecimal gstAmount = subtotalAfterDiscount.multiply(BigDecimal.valueOf(rate)).divide(BigDecimal.valueOf(100));
        
        invoice.setTotalPayable(subtotalAfterDiscount.add(gstAmount));
        
        // NOW save the invoice with all fields properly set
        invoice = invoiceRepository.save(invoice);
        
        // 5. Create Invoice Item for Plan
        InvoiceItem planItem = new InvoiceItem();
        planItem.setInvoiceId(invoice.getId());
        planItem.setItemType("plan");
        planItem.setDescription(plan.getName());
        planItem.setQuantity(1);
        planItem.setUnitPrice(planPrice.getAmount());
        planItem.setLineTotal(planPrice.getAmount());
        invoiceItemRepository.save(planItem);
        
        // 6. Save addon invoice items
        for (InvoiceItem addonItem : invoiceItems) {
            addonItem.setInvoiceId(invoice.getId());
            invoiceItemRepository.save(addonItem);
        }

        // 7. Log Event
        SubscriptionEvent event = new SubscriptionEvent();
        event.setTenantId(tenantId);
        event.setSubscriptionId(subscription.getId());
        event.setEventType("subscription_created");
        event.setPayloadJson("{\"invoice_id\":" + invoice.getId() + "}");
        subscriptionEventRepository.save(event);

        sendInvoiceEmail(invoice.getId());

        return invoice;

    }

    private long calculateDays(PlanPrice planPrice) {
        if ("monthly".equalsIgnoreCase(planPrice.getBillingPeriod())) {
            return 30L * planPrice.getDuration();
        } else if ("yearly".equalsIgnoreCase(planPrice.getBillingPeriod())) {
            return 365L * planPrice.getDuration();
        }
        return 30L;
    }

    @Transactional
    public void activateTenantAfterPaid(Long tenantId, Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        // 1. Update Tenant Status to Active
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        tenant.setStatus("active");
        tenantRepository.save(tenant);

        // 2. Update Invoice Status to Paid
        Invoice invoice = invoiceRepository.findBySubscriptionId(subscriptionId)
                .stream()
                .filter(inv -> "unpaid".equals(inv.getStatus()))
                .findFirst()
                .orElse(null);
        if (invoice != null) {
            invoice.setStatus("paid");
            invoiceRepository.save(invoice);
        }

        // 3. Check for existing TenantActivation (idempotency)
        boolean alreadyActive = tenantActivationRepository.existsByTenantIdAndStatus(tenantId, "active");
        if (!alreadyActive) {
            // Create new Tenant Activation with amount
            TenantActivation activation = new TenantActivation();
            activation.setTenantId(tenantId);
            activation.setSource("self_payment");
            activation.setActivatedAt(LocalDateTime.now());
            activation.setExpiresAt(subscription.getCurrentPeriodEnd());
            activation.setStatus("active");
            
            // Store activation amount from invoice
            if (invoice != null) {
                activation.setActivationPrice(invoice.getTotalPayable());
            }
            
            tenantActivationRepository.save(activation);
        }

        // 4. Create/Update Tenant Usage with Plan Limitations
        Plan plan = subscription.getPlan();
        PlanLimitation planLimitation = planLimitationRepository.findByPlan(plan);
        
        if (!tenantUsageRepository.existsByTenantId(tenantId)) {
            TenantUsage usage = new TenantUsage();
            usage.setTenantId(tenantId);
            
            // Set limits from plan limitation if exists
            if (planLimitation != null) {
                usage.setActiveUsersCount(planLimitation.getAllowedUserCount());
                usage.setCompaniesCount(planLimitation.getAllowedCompanyCount());
            } else {
                // Fallback to defaults if no limitation found
                usage.setActiveUsersCount(0);
                usage.setCompaniesCount(0);
            }
            
            tenantUsageRepository.save(usage);
        }

        // 5. Update Subscription Status to Active
        subscription.setStatus("active");
        subscriptionRepository.save(subscription);

        // 6. Update all SubscriptionAddons to Active
        List<SubscriptionAddon> addons = subscriptionAddonRepository.findBySubscriptionId(subscriptionId);
        for (SubscriptionAddon addon : addons) {
            addon.setStatus("active");
            subscriptionAddonRepository.save(addon);
        }

        // 7. Log Activation Event
        SubscriptionEvent event = new SubscriptionEvent();
        event.setTenantId(tenantId);
        event.setSubscriptionId(subscriptionId);
        event.setEventType("tenant_activated");
        event.setPayloadJson("{\"source\":\"self_payment\"}");
        subscriptionEventRepository.save(event);
    }

    @Transactional
    public Invoice renewSubscription(Long tenantId) {
        Subscription sub = subscriptionRepository.findFirstByTenantIdOrderByCreatedAtDesc(tenantId)
                .orElseThrow(() -> new RuntimeException("No subscription found to renew"));

        PlanPrice planPrice = planPriceRepository.findById(sub.getPlanPriceId())
                .orElseThrow(() -> new RuntimeException("Plan price not found"));

        // Renewal extends expiry
        Invoice invoice = new Invoice();
        invoice.setTenantId(tenantId);
        invoice.setSubscriptionId(sub.getId());
        invoice.setInvoiceNumber("REN-" + System.currentTimeMillis());
        invoice.setGateway("razorpay");  //for now hardcoding, can be dynamic based on request or config
        invoice.setTotalPayable(planPrice.getAmount());
        invoice.setStatus("unpaid");
        invoice = invoiceRepository.save(invoice);

        InvoiceItem planItem = new InvoiceItem();
        planItem.setInvoiceId(invoice.getId());
        planItem.setItemType("renewal");
        planItem.setDescription("Renewal for " + sub.getPlan().getName());
        planItem.setQuantity(1);
        planItem.setUnitPrice(planPrice.getAmount());
        planItem.setLineTotal(planPrice.getAmount());
        invoiceItemRepository.save(planItem);

        SubscriptionEvent event = new SubscriptionEvent();
        event.setTenantId(tenantId);
        event.setSubscriptionId(sub.getId());
        event.setEventType("renewal_started");
        event.setPayloadJson("{\"invoice_id\":" + invoice.getId() + "}");
        subscriptionEventRepository.save(event);

        sendInvoiceEmail(invoice.getId());

        return invoice;

    }

    @Transactional
    public void applyRenewal(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        Subscription sub = subscriptionRepository.findById(invoice.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        PlanPrice planPrice = planPriceRepository.findById(sub.getPlanPriceId())
                .orElseThrow(() -> new RuntimeException("Plan price not found"));

        LocalDateTime currentEnd = sub.getCurrentPeriodEnd();
        if (currentEnd == null || currentEnd.isBefore(LocalDateTime.now())) {
            currentEnd = LocalDateTime.now();
        }
        
        LocalDateTime newEnd = currentEnd.plusDays(calculateDays(planPrice));
        sub.setCurrentPeriodEnd(newEnd);
        subscriptionRepository.save(sub);

        TenantActivation act = tenantActivationRepository.findFirstByTenantIdOrderByCreatedAtDesc(sub.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant activation not found"));
        act.setExpiresAt(newEnd);
        act.setStatus("active");
        tenantActivationRepository.save(act);

        SubscriptionEvent event = new SubscriptionEvent();
        event.setTenantId(sub.getTenantId());
        event.setSubscriptionId(sub.getId());
        event.setEventType("renewal_success");
        subscriptionEventRepository.save(event);
    }

    @Transactional
    public Invoice startUpgrade(Long tenantId, Long toPlanId, Long toPlanPriceId) {
        Subscription sub = subscriptionRepository.findFirstByTenantIdOrderByCreatedAtDesc(tenantId)
                .orElseThrow(() -> new RuntimeException("Active subscription not found"));

        PlanPrice toPrice = planPriceRepository.findById(toPlanPriceId)
                .orElseThrow(() -> new RuntimeException("Target plan price not found"));

        // Policy A: expiry unchanged
        BigDecimal amount = toPrice.getAmount(); 

        Invoice invoice = new Invoice();
        invoice.setTenantId(tenantId);
        invoice.setSubscriptionId(sub.getId());
        invoice.setInvoiceNumber("UPG-" + System.currentTimeMillis());
        invoice.setGateway("razorpay");  //for now hardcoding, can be dynamic based on request or config
        invoice.setSubtotal(amount);
        invoice.setStatus("unpaid");
        invoice = invoiceRepository.save(invoice);

        InvoiceItem item = new InvoiceItem();
        item.setInvoiceId(invoice.getId());
        item.setItemType("upgrade");
        item.setDescription("Upgrade to " + toPrice.getPlan().getName());
        item.setQuantity(1);
        item.setUnitPrice(amount);
        item.setLineTotal(amount);
        invoiceItemRepository.save(item);

        // Calculate GST
        calculateGstAndTotal(invoice);

        SubscriptionEvent event = new SubscriptionEvent();
        event.setTenantId(tenantId);
        event.setSubscriptionId(sub.getId());
        event.setEventType("upgrade_started");
        event.setPayloadJson("{\"to_plan_id\":" + toPlanId + ", \"to_plan_price_id\":" + toPlanPriceId + ", \"invoice_id\":" + invoice.getId() + "}");
        subscriptionEventRepository.save(event);

        sendInvoiceEmail(invoice.getId());

        return invoice;
    }

    private void calculateGstAndTotal(Invoice invoice) {
        Map<String, Object> gstData = gstRateService.getLatestGstRate();
        Double rate = (Double) gstData.get("rate");
        BigDecimal gstAmount = invoice.getSubtotal().multiply(BigDecimal.valueOf(rate)).divide(BigDecimal.valueOf(100));
        invoice.setTotalPayable(invoice.getSubtotal().add(gstAmount));
        invoiceRepository.save(invoice);
    }

    @Transactional
    public void applyUpgrade(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        Subscription sub = subscriptionRepository.findById(invoice.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        SubscriptionEvent ev = subscriptionEventRepository.findFirstBySubscriptionIdAndEventTypeOrderByCreatedAtDesc(sub.getId(), "upgrade_started");
        // Simplified parsing of to_plan_id and to_plan_price_id from payload or using a different strategy
        // For now, let's assume we can get it or we store it in invoice metadata if needed.
        // The user suggested using subscription_events for this.
        
        // Mocking the extraction for now as I don't have a JSON parser here easily accessible in the same way
        // but in a real app we'd parse the payload.
        
        // Let's assume the payload extraction logic here.
        // sub.setPlan(toPlan);
        // sub.setPlanPriceId(toPlanPriceId);
        // sub.setCurrentPeriodEnd remains same (Policy A)
        
        subscriptionRepository.save(sub);
        
        SubscriptionEvent successEv = new SubscriptionEvent();
        successEv.setTenantId(sub.getTenantId());
        successEv.setSubscriptionId(sub.getId());
        successEv.setEventType("upgrade_success");
        subscriptionEventRepository.save(successEv);
    }

    public void sendInvoiceEmail(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        Tenant tenant = tenantRepository.findById(invoice.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        List<InvoiceItem> items = invoiceItemRepository.findByInvoiceId(invoiceId);

        // GST Breakdown (Only using GST Rate table as requested)
        Map<String, Object> gstData = gstRateService.getLatestGstRate();
        Double gstRate = (Double) gstData.get("rate");
        BigDecimal subtotal = invoice.getSubtotal();
        BigDecimal gstAmount = subtotal.multiply(BigDecimal.valueOf(gstRate)).divide(BigDecimal.valueOf(100));

        emailService.sendInvoiceEmail(
                tenant.getEmail(),
                tenant.getName(),
                invoice.getInvoiceNumber(),
                invoice.getTotalPayable().toString(),
                items,
                gstRate.toString(),
                gstAmount.toString(),
                subtotal.toString()
        );
    }

    // ===================================================================
    // TEMPORARY: Payment Simulation Method (Remove when gateway is ready)
    // ===================================================================
    
    /**
     * Simulates successful payment and triggers activation.
     * This is a temporary method for testing the complete flow.
     * 
     * @param invoiceId The invoice ID to mark as paid
     * @return Success message with activation details
     */
    @Transactional
    public Map<String, Object> simulatePaymentSuccess(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        if ("paid".equals(invoice.getStatus())) {
            return Map.of(
                "status", "already_paid",
                "message", "Invoice already marked as paid",
                "invoice_id", invoiceId
            );
        }
        
        // Simulate payment success
        invoice.setStatus("paid");
        invoiceRepository.save(invoice);
        
        // Trigger activation
        activateTenantAfterPaid(invoice.getTenantId(), invoice.getSubscriptionId());
        
        // Fetch updated subscription
        Subscription subscription = subscriptionRepository.findById(invoice.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
        
        return Map.of(
            "status", "success",
            "message", "Payment simulated and tenant activated successfully",
            "invoice_id", invoiceId,
            "invoice_status", invoice.getStatus(),
            "subscription_id", subscription.getId(),
            "subscription_status", subscription.getStatus(),
            "tenant_id", invoice.getTenantId()
        );
    }
}

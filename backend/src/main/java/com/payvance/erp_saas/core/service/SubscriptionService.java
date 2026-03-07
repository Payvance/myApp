package com.payvance.erp_saas.core.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.payvance.erp_saas.core.entity.AddOn;
import com.payvance.erp_saas.core.entity.Invoice;
import com.payvance.erp_saas.core.entity.InvoiceItem;
import com.payvance.erp_saas.core.entity.Plan;
import com.payvance.erp_saas.core.entity.PlanLimitation;
import com.payvance.erp_saas.core.entity.PlanPrice;
import com.payvance.erp_saas.core.entity.ReferralProgram;
import com.payvance.erp_saas.core.entity.Subscription;
import com.payvance.erp_saas.core.entity.SubscriptionAddon;
import com.payvance.erp_saas.core.entity.SubscriptionEvent;
import com.payvance.erp_saas.core.entity.Tenant;
import com.payvance.erp_saas.core.entity.TenantActivation;
import com.payvance.erp_saas.core.entity.TenantUsage;
import com.payvance.erp_saas.core.repository.ActivationKeyRepository;
import com.payvance.erp_saas.core.repository.AddOnRepository;
import com.payvance.erp_saas.core.repository.CouponRepository;
import com.payvance.erp_saas.core.repository.InvoiceItemRepository;
import com.payvance.erp_saas.core.repository.InvoiceRepository;
import com.payvance.erp_saas.core.repository.PlanLimitationRepository;
import com.payvance.erp_saas.core.repository.PlanPriceRepository;
import com.payvance.erp_saas.core.repository.PlanRepository;
import com.payvance.erp_saas.core.repository.ReferralCodeRepository;
import com.payvance.erp_saas.core.repository.ReferralProgramRepository;
import com.payvance.erp_saas.core.repository.ReferralRepository;
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

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

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
    private final ActivationKeyService activationKeyService;
    private final ActivationKeyRepository activationKeyRepository;
    private final CouponService couponService;
    private final ReferralProgramService referralProgramService;
    private final WalletService walletService;
    private final CouponRepository couponRepository;
    private final ReferralRepository referralRepository;
    private final ReferralProgramRepository referralProgramRepository;
    private final ReferralCodeRepository referralCodeRepository;


    public enum SubscriptionOperation {
        FIRST_TIME_PAYMENT,
        UPGRADE_PLAN,
        RENEWAL_PLAN,
        ADDON_PURCHASE,
        PAY_WITHOUT_TRAIL,
        NO_MATCH
    }

    @Transactional
    public Invoice startSubscription(Long tenantId, Long planId, List<Map<String, Object>> addonRequests, BigDecimal discountAmount, String referralCode) {
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
        invoice.setGateway("cashfree");
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
        
        // Use discountBy for persistence without schema changes
        StringBuilder metadata = new StringBuilder();
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            metadata.append("wallet_usage:").append(discountAmount);
        }
        if (referralCode != null && !referralCode.isEmpty()) {
            if (metadata.length() > 0) metadata.append("|");
            metadata.append("referral_code:").append(referralCode);
        }
        
        if (metadata.length() > 0) {
            invoice.setDiscountBy(metadata.toString());
        }
        
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

        // 2. Resolve Invoice for metadata (Price, Currency)
        Invoice invoice = invoiceRepository.findBySubscriptionId(subscriptionId)
                .stream()
                .filter(inv -> "paid".equals(inv.getStatus()) || "unpaid".equals(inv.getStatus()))
                .findFirst()
                .orElse(null);
        
        logger.info("[DEBUG] Activation Flow - Invoice ID: {}, Status: {}", 
                (invoice != null ? invoice.getId() : "NOT_FOUND"),
                (invoice != null ? invoice.getStatus() : "N/A"));

        // 3. Handle activation and license key
        // Generate System Activation Key upon subscription.
        // This will throw an error if the tenant already has an active key.
        logger.info("[DEBUG] Starting Key Generation for tenantId: {}", tenantId);
        java.util.Map<String, String> keyData = activationKeyService.generateSystemKeyForTenant(
                tenantId,
                tenant.getEmail(),
                tenant.getPhone()
        );
        
        String plainKey = keyData.get("plainKey");
        String keyHash = keyData.get("hash");
        logger.info("[DEBUG] Key Generated Successfully. plainKey prefix: {}", (plainKey != null ? plainKey.substring(0, 4) : "NULL"));

        // Check for existing TenantActivation to decide whether to create or update
        java.util.List<TenantActivation> existingActivations = tenantActivationRepository.findByTenantIdAndStatus(tenantId, "active");
        
        if (existingActivations.isEmpty()) {
            // Create new Tenant Activation
            TenantActivation activation = new TenantActivation();
            activation.setTenantId(tenantId);
            activation.setSource("self_payment");
            attachmentActivationFields(activation, subscription, invoice, keyHash);
            tenantActivationRepository.save(activation);
        } else {
            // Update the most recent active activation
            TenantActivation activation = existingActivations.get(0);
            attachmentActivationFields(activation, subscription, invoice, keyHash);
            tenantActivationRepository.save(activation);
        }

        // Send License Key Email with plain key
        try {
            logger.info("[DEBUG] Attempting to send License Key Email to: {}", tenant.getEmail());
            emailService.sendLicenseIssuedEmailSync(
                    tenant.getEmail(),
                    null,
                    plainKey
            );
            logger.info("[DEBUG] License Key Email Sent Successfully.");
        } catch (Exception e) {
            logger.error("[ERROR] Failed to send License Key Email: {}. But the key is saved in DB.", e.getMessage());
            // We don't rethrow because we want the transaction (key and activation) to COMMIT.
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
        invoice.setGateway("cashfree");
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
        
        PlanPrice currentPrice = planPriceRepository.findById(sub.getPlanPriceId())
                .orElseThrow(() -> new RuntimeException("Current plan price not found"));

        // Differential pricing: charge only the difference between new and old plan
        BigDecimal upgradeAmount = toPrice.getAmount().subtract(currentPrice.getAmount());
        if (upgradeAmount.compareTo(BigDecimal.ZERO) < 0) {
            upgradeAmount = BigDecimal.ZERO; // No refund for downgrade in this simplified logic
        }

        Invoice invoice = new Invoice();
        invoice.setTenantId(tenantId);
        invoice.setSubscriptionId(sub.getId());
        invoice.setInvoiceNumber("UPG-" + System.currentTimeMillis());
        invoice.setGateway("razorpay");
        invoice.setSubtotal(upgradeAmount);
        invoice.setStatus("unpaid");
        invoice = invoiceRepository.save(invoice);

        InvoiceItem item = new InvoiceItem();
        item.setInvoiceId(invoice.getId());
        item.setItemType("upgrade");
        item.setDescription("Upgrade from " + currentPrice.getPlan().getName() + " to " + toPrice.getPlan().getName());
        item.setQuantity(1);
        item.setUnitPrice(upgradeAmount);
        item.setLineTotal(upgradeAmount);
        invoiceItemRepository.save(item);

        calculateGstAndTotal(invoice);

        SubscriptionEvent event = new SubscriptionEvent();
        event.setTenantId(tenantId);
        event.setSubscriptionId(sub.getId());
        event.setEventType("upgrade_started");
        // Store target plan details in payload for applyUpgrade to use
        event.setPayloadJson(String.format("{\"to_plan_id\":%d, \"to_plan_price_id\":%d, \"invoice_id\":%d}", 
                toPlanId, toPlanPriceId, invoice.getId()));
        subscriptionEventRepository.save(event);

    

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
        
        if (ev != null && ev.getPayloadJson() != null) {
            try {
                // Simple parsing of JSON payload
                String payload = ev.getPayloadJson();
                Long toPlanId = extractLong(payload, "to_plan_id");
                Long toPlanPriceId = extractLong(payload, "to_plan_price_id");

                Plan toPlan = planRepository.findById(toPlanId)
                        .orElseThrow(() -> new RuntimeException("Target plan not found"));
                
                sub.setPlan(toPlan);
                sub.setPlanPriceId(toPlanPriceId);
                // Expiry remains unchanged as requested
                subscriptionRepository.save(sub);

                // Log success
                SubscriptionEvent successEv = new SubscriptionEvent();
                successEv.setTenantId(sub.getTenantId());
                successEv.setSubscriptionId(sub.getId());
                successEv.setEventType("upgrade_success");
                subscriptionEventRepository.save(successEv);

            } catch (Exception e) {
                logger.error("Failed to apply upgrade: {}", e.getMessage());
                throw new RuntimeException("Failed to finalize upgrade switch", e);
            }
        }
    }

    private Long extractLong(String json, String key) {
        String pattern = "\"" + key + "\":\\s?(\\d+)";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(json);
        if (m.find()) {
            return Long.parseLong(m.group(1));
        }
        return null;
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
    

    private void attachmentActivationFields(TenantActivation activation, Subscription subscription, Invoice invoice, String keyHash) {
        activation.setActivatedAt(LocalDateTime.now());
        activation.setExpiresAt(subscription.getCurrentPeriodEnd());
        activation.setStatus("active");
        activation.setActivationCodeHash(keyHash);
        if (invoice != null) {
            activation.setActivationPrice(invoice.getTotalPayable());
            activation.setCurrency(invoice.getCurrency());
        }
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getActivePlanDetails(Long tenantId) {

        Subscription subscription = subscriptionRepository
                .findByTenantIdAndStatus(tenantId, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("No active subscription found"));

        Plan plan = subscription.getPlan();

        Map<String, Object> response = new HashMap<>();

        response.put("subscriptionId", subscription.getId());
        response.put("startDate", subscription.getStartAt());
        response.put("endDate", subscription.getCurrentPeriodEnd());
        response.put("status", subscription.getStatus());

        // PLAN DETAILS
        Map<String, Object> planDetails = new HashMap<>();
        planDetails.put("planId", plan.getId());
        planDetails.put("planName", plan.getName());
        planDetails.put("planCode", plan.getCode());
        planDetails.put("isActive", plan.getIsActive());

        // FETCH PLAN PRICE
        PlanPrice planPrice = planPriceRepository.findByPlanId(plan.getId()).orElse(null);

        if (planPrice != null) {
            Map<String, Object> priceDetails = new HashMap<>();
            priceDetails.put("priceId", planPrice.getId());
            priceDetails.put("billingPeriod", planPrice.getBillingPeriod());
            priceDetails.put("duration", planPrice.getDuration());
            priceDetails.put("currency", planPrice.getCurrency());
            priceDetails.put("amount", planPrice.getAmount());

            planDetails.put("price", priceDetails);
        }

        response.put("plan", planDetails);

        // FETCH SUBSCRIPTION ADDONS
        List<SubscriptionAddon> subscriptionAddons =
                subscriptionAddonRepository.findBySubscriptionIdAndStatus(subscription.getId(), "ACTIVE");

        List<Map<String, Object>> addonList = new ArrayList<>();

        for (SubscriptionAddon sa : subscriptionAddons) {

            AddOn addon = addonRepository.findById(sa.getAddonId()).orElse(null);

            if (addon != null) {

                Map<String, Object> addonData = new HashMap<>();

                addonData.put("addonId", addon.getId());
                addonData.put("code", addon.getCode());
                addonData.put("name", addon.getName());
                addonData.put("currency", addon.getCurrency());
                addonData.put("unit", addon.getUnit());
                addonData.put("unitPrice", addon.getUnitPrice());
                addonData.put("status", addon.getStatus());

                addonData.put("quantity", sa.getQuantity());
                addonData.put("effectiveFrom", sa.getEffectiveFrom());
                addonData.put("effectiveTo", sa.getEffectiveTo());

                addonList.add(addonData);
            }
        }

        response.put("addons", addonList);

        return response;
    }

    /**
     * Entry point for processing subscriptions based on tenant state.
     */
    @Transactional
    public Invoice processSubscription(Long tenantId, Long planId, List<Map<String, Object>> addons, 
                                     String couponCode, String referralCode, BigDecimal walletAmount) {
        
        SubscriptionOperation operation = determineSubscriptionOperation(tenantId, planId, addons);
        
        logger.info("[SUBSCRIPTION] Processing operation: {} for tenant: {}", operation, tenantId);

        switch (operation) {
            case FIRST_TIME_PAYMENT:
                return handleFirstTimePayment(tenantId, planId, addons, couponCode, referralCode, walletAmount);
            case UPGRADE_PLAN:
                return handleUpgradePlan(tenantId, planId, addons, couponCode, referralCode, walletAmount);
            case RENEWAL_PLAN:
                return handleRenewalPlan(tenantId, planId, addons, couponCode, referralCode, walletAmount);
            case ADDON_PURCHASE:
                return handleAddonPurchase(tenantId, planId, addons, couponCode, referralCode, walletAmount);
            case PAY_WITHOUT_TRAIL:
                return handlePayWithoutTrail(tenantId, planId, addons, couponCode, referralCode, walletAmount);
            default:
                throw new RuntimeException("No matching subscription operation found for tenant state");
        }
    }

    /**
     * Determines the type of subscription operation needed.
     */
    private SubscriptionOperation determineSubscriptionOperation(Long tenantId, Long requestedPlanId, List<Map<String, Object>> addons) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        boolean keyExists = activationKeyRepository.existsByRedeemedTenantId(tenantId);
        Optional<TenantActivation> latestActivationOpt = tenantActivationRepository.findFirstByTenantIdOrderByCreatedAtDesc(tenantId);
        Optional<Subscription> latestSubOpt = subscriptionRepository.findFirstByTenantIdOrderByCreatedAtDesc(tenantId);
        
        String tenantStatus = tenant.getStatus();
        
        // 1. First Time Payment: pending payment/trial and no activation key
        if (("payment_pending".equalsIgnoreCase(tenantStatus) || "trial".equalsIgnoreCase(tenantStatus)) && !keyExists) {
            return SubscriptionOperation.FIRST_TIME_PAYMENT;
        }
        
        // 2. Pay Without Trail: inactive and no activation key (didn't take trial)
        if ("inactive".equalsIgnoreCase(tenantStatus) && !keyExists) {
            return SubscriptionOperation.PAY_WITHOUT_TRAIL;
        }

        if (latestActivationOpt.isPresent() && latestSubOpt.isPresent()) {
            TenantActivation activation = latestActivationOpt.get();
            Subscription currentSub = latestSubOpt.get();
            LocalDateTime now = LocalDateTime.now();
            boolean isExpired = activation.getExpiresAt() != null && activation.getExpiresAt().isBefore(now);
            
            // 3. Renewal: Activation key exists AND [is actually expired OR in payment_pending state for extension]
            if (keyExists && (isExpired || "payment_pending".equalsIgnoreCase(tenantStatus))) {
                 return SubscriptionOperation.RENEWAL_PLAN;
            }

            // 4. Active scenarios (PRE-EXPIRY)
            if (keyExists && "active".equalsIgnoreCase(tenantStatus) && !isExpired) {
                // If plan is different -> Upgrade/Plan Change
                if (!currentSub.getPlan().getId().equals(requestedPlanId)) {
                    return SubscriptionOperation.UPGRADE_PLAN;
                }
                // If plan is same but addons provided -> Addon purchase
                if (addons != null && !addons.isEmpty()) {
                    return SubscriptionOperation.ADDON_PURCHASE;
                }
                
                // If plan is same and no addons -> User is trying to "renew" early.
                // Business rule: "renewal will done only after expiry otherwise it would be upgrade"
                return SubscriptionOperation.UPGRADE_PLAN; 
            }
        }

        // Fallback for inactive results with key -> Treat as Renewal
        if (keyExists && "inactive".equalsIgnoreCase(tenantStatus)) {
            return SubscriptionOperation.RENEWAL_PLAN;
        }

        return SubscriptionOperation.NO_MATCH;
    }

    private Invoice handleAddonPurchase(Long tenantId, Long planId, List<Map<String, Object>> addons, 
                                      String couponCode, String referralCode, BigDecimal walletAmount) {
        return startSubscription(tenantId, planId, addons, BigDecimal.ZERO, null);
    }

    private Invoice handleFirstTimePayment(Long tenantId, Long planId, List<Map<String, Object>> addons, 
                                         String couponCode, String referralCode, BigDecimal walletAmount) {
        BigDecimal couponDiscount = BigDecimal.ZERO;
        
        if (couponCode != null && !couponCode.isEmpty()) {
            Optional<com.payvance.erp_saas.core.entity.Coupon> couponOpt = couponRepository.findByCode(couponCode);
            if (couponOpt.isPresent()) {
                com.payvance.erp_saas.core.entity.Coupon coupon = couponOpt.get();
                if ("ACTIVE".equalsIgnoreCase(coupon.getStatus())) {
                    Plan plan = planRepository.findById(planId).orElse(null);
                    BigDecimal baseAmount = (plan != null) ? plan.getPlanPrice().getAmount() : BigDecimal.ZERO;

                    if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
                        couponDiscount = baseAmount.multiply(BigDecimal.valueOf(coupon.getDiscountPercentage()))
                                .divide(BigDecimal.valueOf(100));
                    } else if ("FLAT".equalsIgnoreCase(coupon.getDiscountType())) {
                        couponDiscount = BigDecimal.valueOf(coupon.getDiscountValue());
                    }
                }
            }
        }

        BigDecimal totalReduction = couponDiscount.add(walletAmount != null ? walletAmount : BigDecimal.ZERO);
        return startSubscription(tenantId, planId, addons, totalReduction, referralCode);
    }

    private Invoice handleUpgradePlan(Long tenantId, Long planId, List<Map<String, Object>> addons, 
                                    String couponCode, String referralCode, BigDecimal walletAmount) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Target plan not found"));
        return startUpgrade(tenantId, planId, plan.getPlanPrice().getId());
    }

    private Invoice handleRenewalPlan(Long tenantId, Long planId, List<Map<String, Object>> addons, 
                                    String couponCode, String referralCode, BigDecimal walletAmount) {
        return renewSubscription(tenantId);
    }

    private Invoice handlePayWithoutTrail(Long tenantId, Long planId, List<Map<String, Object>> addons, 
                                        String couponCode, String referralCode, BigDecimal walletAmount) {
        BigDecimal discount = walletAmount != null ? walletAmount : BigDecimal.ZERO;
        return startSubscription(tenantId, planId, addons, discount, referralCode);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReferral(Long referredTenantId, String referralCode) {
        if (referralCode == null || referralCode.isEmpty()) return;

        logger.info("Processing referral reward for code: {} used by tenant: {}", referralCode, referredTenantId);

        Optional<com.payvance.erp_saas.core.entity.ReferralCode> codeOpt = referralCodeRepository.findByCodeAndStatus(referralCode, "active");
        if (codeOpt.isPresent()) {
            com.payvance.erp_saas.core.entity.ReferralCode refCode = codeOpt.get();
            ReferralProgram program = referralProgramRepository.findById(refCode.getProgramId())
                    .orElseThrow(() -> new RuntimeException("Referral Program not found"));

            // Get the subscription amount for percentage calculation if needed
            Subscription sub = subscriptionRepository.findFirstByTenantIdOrderByCreatedAtDesc(referredTenantId).orElse(null);
            double baseAmount = 0.0;
            if (sub != null) {
                baseAmount = sub.getPlan().getPlanPrice().getAmount().doubleValue();
            }

            BigDecimal rewardAmount = BigDecimal.ZERO;
            if ("FLAT".equalsIgnoreCase(program.getRewardType())) {
                rewardAmount = BigDecimal.valueOf(program.getRewardValue());
            } else if ("PERCENTAGE".equalsIgnoreCase(program.getRewardType())) {
                rewardAmount = BigDecimal.valueOf(baseAmount * (program.getRewardPercentage() / 100.0));
            }

            // Create Referral Record
            com.payvance.erp_saas.core.entity.Referral referral = new com.payvance.erp_saas.core.entity.Referral();
            referral.setReferrerTenantId(refCode.getOwnerId());
            referral.setReferredTenantId(referredTenantId);
            referral.setReferrerId(refCode.getOwnerId()); // Simplified for now
            referral.setRewardedAmount(rewardAmount);
            referral.setStatus("PAID_PENDING"); // Or "COMPLETED" if wallet is updated immediately
            referralRepository.save(referral);

            // Update usage count
            refCode.setUsedCount(refCode.getUsedCount() + 1);
            referralCodeRepository.save(refCode);

            logger.info("Referral reward of {} created for referrer tenant: {}", rewardAmount, refCode.getOwnerId());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deductWalletBalance(Long tenantId, BigDecimal amount, String referenceType, Long referenceId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return;
        
        logger.info("Deducting {} from tenant {} wallet for {} ID: {}", amount, tenantId, referenceType, referenceId);
        walletService.deductBalance(tenantId, amount, referenceType, referenceId);
    }
}

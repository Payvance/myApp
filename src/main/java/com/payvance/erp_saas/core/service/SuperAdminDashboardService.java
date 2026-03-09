package com.payvance.erp_saas.core.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.payvance.erp_saas.core.repository.CaRepository;
import com.payvance.erp_saas.core.repository.InvoiceRepository;
import com.payvance.erp_saas.core.repository.PlanRepository;
import com.payvance.erp_saas.core.repository.SubscriptionRepository;
import com.payvance.erp_saas.core.repository.TenantActivationRepository;
import com.payvance.erp_saas.core.repository.TenantRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
import com.payvance.erp_saas.core.repository.VendorActivationBatchRepository;
import com.payvance.erp_saas.core.repository.VendorRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for Superadmin Dashboard Data
 * Uses existing repositories to fetch actual data
 * 
 * @author system
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class SuperAdminDashboardService {
    
    private final TenantRepository tenantRepository;
    private final VendorRepository vendorRepository;
    private final CaRepository caRepository;
    private final PlanRepository planRepository;
    private final VendorActivationBatchRepository vendorActivationBatchRepository;
    private final TenantActivationRepository tenantActivationRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    
    /**
     * Get complete dashboard data for Superadmin
     */
    public Map<String, Object> getDashboardData(Integer startYear, Integer endYear) {
        Map<String, Object> data = new HashMap<>();
        
        // Use current financial year if not provided
        if (startYear == null) {
            LocalDate now = LocalDate.now();
            startYear = now.getMonthValue() >= 4 ? now.getYear() : now.getYear() - 1;
        }
        if (endYear == null) endYear = startYear + 1;

        // Calculate boundary dates for financial year (April to March)
        LocalDateTime startDate = LocalDateTime.of(startYear, 4, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(endYear, 3, 31, 23, 59, 59);

        // Get card data values
        Long totalTenants = tenantRepository.getTotalTenants();
        Long totalVendors = vendorRepository.getTotalVendors();
        Long totalCAs = caRepository.getTotalCAs();
        Long vendorApprovalRequests = vendorRepository.countByStatus("pending_approval");
        Long caApprovalRequests = caRepository.countByStatus("pending_approval");
        Long vendorBatchesPendingApproval = vendorActivationBatchRepository.countByStatus("Pending");
        Long activePlans = planRepository.countByIsActive(1);
        Long totalRevenueGenerated = getTotalRevenueGenerated();
        Long guestUsers = userRepository.countGuestUsers();
        
        // Cards data
        data.put("cards", Arrays.asList(
            createCard("total_tenants", "Total Tenants", formatCount(totalTenants), "bi-people", "primary"),
            createCard("total_vendors", "Total Vendors", formatCount(totalVendors), "bi-building", "success"),
            createCard("total_cas", "Total CA's", formatCount(totalCAs), "bi-person-badge", "primary"),
            createCard("vendor_approval_requests", "Vendor Approval Requests", formatCount(vendorApprovalRequests), "bi-hourglass-split", "warning"),
            createCard("ca_approval_requests", "CA Approval Requests", formatCount(caApprovalRequests), "bi-hourglass-split", "warning"),
            createCard("vendor_batches_pending_approval", "Vendor Batches Pending Approval", formatCount(vendorBatchesPendingApproval), "bi-hourglass-split", "warning"),
            createCard("active_plans", "Active Plans", formatCount(activePlans), "bi-clock-history", "danger"),
            createCard("total_revenue_generated", "Total Revenue Generated", "₹" + formatAmount(totalRevenueGenerated), "bi-currency-rupee", "success"),
            createCard("guest_users", "Guest Users", formatCount(guestUsers), "bi-people", "danger")
        ));
        
        // Pie charts data
        data.put("pieCharts", Arrays.asList(
            Map.of("id", "highest_selling_plans", "title", "Highest Selling Plans",
                "data", getHighestSellingPlansData()
            ),
            Map.of("id", "sale_vs_renewal", "title", "New Sales vs Renew",
                "data", getSaleVsRenewalData(startDate, endDate)
            )
        ));

        // Data views
        List<Map<String, Object>> dataViews = new ArrayList<>();
        
        Map<String, Object> batchView = new HashMap<>();
        batchView.put("id", "vendor_batch_tenants");
        batchView.put("title", "Vendor Batch & Tenant Details");
        batchView.put("data", getVendorBatchTenantDetails());
        dataViews.add(batchView);
        
        Map<String, Object> tenantView = new HashMap<>();
        tenantView.put("id", "detailed_tenants");
        tenantView.put("title", "Detailed Tenant Overview");
        tenantView.put("data", getDetailedTenantData());
        dataViews.add(tenantView);
        
        data.put("dataViews", dataViews);
        
        // Bar charts data
        data.put("barCharts", Arrays.asList(
            Map.of("id", "revenue_breakdown", "title", "Revenue Breakdown (Tenants vs Vendors)", 
                "xAxis", "month", "stacked", false,
                "data", getRevenueBreakdownData(startDate, endDate)
            )
        ));
        
        return data;
    }

    private Map<String, Object> createCard(String id, String title, String value, String icon, String color) {
        Map<String, Object> card = new HashMap<>();
        card.put("id", id);
        card.put("title", title);
        card.put("value", value);
        card.put("icon", icon);
        card.put("color", color);
        return card;
    }

    private String formatCount(Long count) {
        return count != null ? String.format("%,d", count) : "0";
    }

    private String formatAmount(Long amount) {
        return amount != null ? String.format("%,d", amount) : "0";
    }

    private List<Map<String, Object>> getHighestSellingPlansData() {
        List<Object[]> results = subscriptionRepository.findPlanPopularity();
        if (results.isEmpty()) {
            return Arrays.asList(Map.of("name", "No Data", "value", 0));
        }
        return results.stream()
            .map(row -> Map.of("name", row[0].toString(), "value", row[1]))
            .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getSaleVsRenewalData(LocalDateTime startDate, LocalDateTime endDate) {
        Long newSales = tenantActivationRepository.countNewSales(startDate, endDate);
        Long renewals = tenantActivationRepository.countRenewals(startDate, endDate);
        
        if ((newSales == null || newSales == 0) && (renewals == null || renewals == 0)) {
            return Arrays.asList(Map.of("name", "No Data", "value", 0));
        }
        
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(Map.of("name", "New Sales", "value", newSales != null ? newSales : 0));
        data.add(Map.of("name", "Renew", "value", renewals != null ? renewals : 0));
        
        return data;
    }

    private List<Map<String, Object>> getVendorBatchTenantDetails() {
        List<Object[]> results = vendorActivationBatchRepository.findBatchWithTenantDetails();
        if (results.isEmpty()) {
            return Arrays.asList(Map.of("batchId", "No Data", "tenantId", "No Data"));
        }
        
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("batchId", row[0] != null ? row[0].toString() : "N/A");
            map.put("vendorId", row[1] != null ? row[1].toString() : "N/A");
            map.put("batchPlan", row[2] != null ? row[2] : "N/A");
            map.put("total", row[3] != null ? row[3] : 0);
            map.put("used", row[4] != null ? row[4] : 0);
            map.put("cost", row[5] != null ? "₹" + formatAmount(((BigDecimal)row[5]).longValue()) : "₹0");
            map.put("tenantId", row[6] != null ? row[6].toString() : "N/A");
            map.put("activationDate", row[7] != null ? ((LocalDateTime)row[7]).toLocalDate().toString() : "N/A");
            map.put("activationStatus", row[8] != null ? row[8] : "N/A");
            map.put("tenantName", row[9] != null ? row[9] : "N/A");
            map.put("tenantEmail", row[10] != null ? row[10] : "N/A");
            map.put("currentPlan", row[11] != null ? row[11] : "No Plan");
            map.put("allowedUsers", row[12] != null ? row[12] : "N/A");
            map.put("allowedCompanies", row[13] != null ? row[13] : "N/A");
            map.put("billingPeriod", row[14] != null ? row[14] : "N/A");
            map.put("planDuration", row[15] != null ? row[15] : "N/A");
            return map;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getDetailedTenantData() {
        List<Object[]> results = tenantRepository.findDetailedTenants();
        if (results.isEmpty()) {
            return new ArrayList<>();
        }
        
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", row[0] != null ? row[0].toString() : "N/A");
            map.put("tenantName", row[1] != null ? row[1] : "N/A");
            map.put("tenantEmail", row[2] != null ? row[2] : "N/A");
            map.put("status", row[3] != null ? row[3] : "N/A");
            map.put("adminName", row[4] != null ? row[4] : "Not Assigned");
            map.put("adminEmail", row[5] != null ? row[5] : "N/A");
            map.put("lastSynced", row[6] != null ? row[6].toString() : "Never");
            map.put("currentPlan", row[7] != null ? row[7] : "No Plan");
            
            // Note: usersCount and companiesCount are removed from this specific query
            // to focus strictly on Admin contacts in the main dashboard view.
            
            // 🆕 Fetch all users for this tenant
            Long tenantId = Long.valueOf(map.get("tenantId").toString());
            List<Map<String, Object>> tenantUsers = userRepository.findTenantUsersByTenantId(tenantId, PageRequest.of(0, 100))
                .getContent().stream().map(u -> {
                    Map<String, Object> uMap = new HashMap<>();
                    uMap.put("name", u.getName());
                    uMap.put("email", u.getEmail());
                    uMap.put("status", u.getTenantUserActive()? "Active" : "Inactive");
                    uMap.put("phone", u.getPhone() != null ? u.getPhone() : "N/A");
                    return uMap;
                }).collect(Collectors.toList());
            map.put("users", tenantUsers);
            map.put("usersCount", tenantUsers.size());
            
            return map;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getRevenueBreakdownData(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> tenantResults = tenantActivationRepository.findMonthlyRevenue(startDate, endDate);
        List<Object[]> vendorResults = vendorActivationBatchRepository.findMonthlyRevenue(startDate, endDate);

        Map<Integer, BigDecimal> tenantMonthlyMap = tenantResults.stream()
            .collect(Collectors.toMap(
                row -> ((Number) row[0]).intValue(),
                row -> (BigDecimal) row[1],
                BigDecimal::add
            ));

        Map<Integer, BigDecimal> vendorMonthlyMap = vendorResults.stream()
            .collect(Collectors.toMap(
                row -> ((Number) row[0]).intValue(),
                row -> (BigDecimal) row[1],
                BigDecimal::add
            ));

        // Use Financial Year ordering (Apr to Mar)
        String[] months = {"Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Mar"};
        int[] monthNumbers = {4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2, 3};

        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", months[i]);
            monthData.put("Tenants", tenantMonthlyMap.getOrDefault(monthNumbers[i], BigDecimal.ZERO).longValue());
            monthData.put("Vendors", vendorMonthlyMap.getOrDefault(monthNumbers[i], BigDecimal.ZERO).longValue());
            data.add(monthData);
        }
        return data;
    }
    
    /**
     * Get total revenue generated from approved batches and active activations
     */
    public Long getTotalRevenueGenerated() {
        BigDecimal approvedBatchesSum = vendorActivationBatchRepository.sumCostPriceByStatus("approved");
        BigDecimal activeActivationsSum = tenantActivationRepository.sumActivationPriceByStatus("active");
        
        BigDecimal totalRevenue = BigDecimal.ZERO;
        if (approvedBatchesSum != null) {
            totalRevenue = totalRevenue.add(approvedBatchesSum);
        }
        if (activeActivationsSum != null) {
            totalRevenue = totalRevenue.add(activeActivationsSum);
        }
        
        return totalRevenue.longValue();
    }
}


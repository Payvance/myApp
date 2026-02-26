package com.payvance.erp_saas.core.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.payvance.erp_saas.core.repository.ActivationKeyRepository;
import com.payvance.erp_saas.core.repository.TenantActivationRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
import com.payvance.erp_saas.core.repository.VendorActivationBatchRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for Vendor Dashboard Data
 * TODO: Implement with actual repository methods
 * 
 * @author system
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class VendorDashboardService {
    
    private final VendorActivationBatchRepository vendorActivationBatchRepository;
    private final UserRepository userRepository;
    private final ActivationKeyRepository activationKeyRepository;
    private final TenantActivationRepository tenantActivationRepository;
    
    /**
     * Get complete dashboard data for Vendor
     * TODO: Implement with actual repository methods
     */
    public Map<String, Object> getDashboardData(Long userId, Integer startYear, Integer endYear) {
        Map<String, Object> data = new HashMap<>();
        
        // Get activation data once to avoid multiple repository calls
        Long totalActivations = vendorActivationBatchRepository.sumTotalActivationsByVendorIdAndStatus(userId, "approved");
        Long usedActivations = vendorActivationBatchRepository.sumUsedActivationsByVendorIdAndStatus(userId, "approved");
        Long remainingActivations = totalActivations - usedActivations;
        
        // Calculate total revenue from cost_price and resale_price (single query)
        Map<String, BigDecimal> revenueData = vendorActivationBatchRepository.sumCostAndResalePriceByVendorIdAndStatus(userId, "approved");
        BigDecimal totalCostPrice = revenueData != null ? revenueData.get("totalCostPrice") : null;
        BigDecimal totalResalePrice = revenueData != null ? revenueData.get("totalResalePrice") : null;
        BigDecimal totalRevenue = (totalCostPrice != null && totalResalePrice != null) ? totalResalePrice.subtract(totalCostPrice) : BigDecimal.ZERO; // resale - cost
        
        // Get additional card data values
        Long pendingBatches = vendorActivationBatchRepository.countByVendorIdAndStatus(userId, "pending");
        Long approvedBatches = vendorActivationBatchRepository.countByVendorIdAndStatus(userId, "approved");
        Long rejectedBatches = vendorActivationBatchRepository.countByVendorIdAndStatus(userId, "rejected");
        
        // Cards data - formatted for frontend with null handling
        data.put("cards", Arrays.asList(
            Map.of("id", "total_activations", "title", "Total License", "value", totalActivations != null ? totalActivations.toString() : "0", "icon", "bi-collection", "color", "success"),
            Map.of("id", "used_activations", "title", "Used License", "value", usedActivations != null ? usedActivations.toString() : "0", "icon", "bi-check2-square", "color", "warning"),
            Map.of("id", "remaining_activations", "title", "Remaining License", "value", remainingActivations != null ? remainingActivations.toString() : "0", "icon", "bi-clock-history", "color", "danger"),
            Map.of("id", "batch_approval_requests", "title", "Batch Approval Requests", "value", pendingBatches != null ? pendingBatches.toString() : "0", "icon", "bi-hourglass-split", "color", "primary"),
            Map.of("id", "approved_batches", "title", "Approved Batches", "value", approvedBatches != null ? approvedBatches.toString() : "0", "icon", "bi-check-circle", "color", "success"),
            Map.of("id", "rejected_batches", "title", "Rejected Batches", "value", rejectedBatches != null ? rejectedBatches.toString() : "0", "icon", "bi-x-circle", "color", "danger"),
            Map.of("id", "total_profit", "title", "Estimated Profit", "value", totalRevenue != null ? totalRevenue.toString() : "0", "icon", "bi-currency-rupee", "color", "success")
        ));
        
        // Renewal vs New Sales Data
        java.util.List<String> salesTypes = activationKeyRepository.getSalesTypesByVendorId(userId);
        long renewalCount = salesTypes.stream().filter("RENEWAL"::equals).count();
        long newSaleCount = salesTypes.stream().filter("NEW_SALE"::equals).count();
        
        List<Map<String, Object>> highestSellingPlans = vendorActivationBatchRepository.findHighestSellingPlansByVendorId(userId);
        
        // Pie charts data
        data.put("pieCharts", Arrays.asList(
            Map.of("id", "renewal_vs_newsales", "title", "Renewal vs New Sales",
                "data", Arrays.asList(
                    Map.of("name", "Renewal", "value", renewalCount),
                    Map.of("name", "New Sales", "value", newSaleCount)
                )
            ),
            Map.of("id", "highest_selling_plans", "title", "Highest Selling Plans",
                "data", highestSellingPlans
            )
        ));
        
        // --- Monthly Revenue (Keys Used) logic for Financial Year (e.g., 2025-26) ---
        int fyStartYear = (startYear != null) ? startYear : 2025; 
        int fyEndYear = (endYear != null) ? endYear : fyStartYear + 1;
        
        java.time.LocalDateTime startDate = java.time.LocalDateTime.of(fyStartYear, 4, 1, 0, 0); // April 1st of startYear
        java.time.LocalDateTime endDate = java.time.LocalDateTime.of(fyEndYear, 4, 1, 0, 0); // April 1st of endYear (exclusive)
        
        List<Map<String, Object>> monthlyData = vendorActivationBatchRepository.findMonthlyKeysUsedByVendorId(userId, startDate, endDate);
        
        // Map database results (Month number -> Count)
        Map<Integer, Long> monthlyCounts = new HashMap<>();
        for (Map<String, Object> record : monthlyData) {
            Number monthObj = (Number) record.get("month");
            Number countObj = (Number) record.get("count");
            if (monthObj != null && countObj != null) {
                monthlyCounts.put(monthObj.intValue(), countObj.longValue());
            }
        }
        
        // Financial Year months sequence: April to March
        String[] fyMonths = {"Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Mar"};
        int[] fyMonthNumbers = {4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2, 3};
        
        List<Map<String, Object>> barChartDataList = new java.util.ArrayList<>();
        for (int i = 0; i < fyMonths.length; i++) {
        	long revenueCount = monthlyCounts.getOrDefault(fyMonthNumbers[i], 0L);
        	barChartDataList.add(Map.of("month", fyMonths[i], "revenue", revenueCount));
        }

        // Bar charts data
        data.put("barCharts", Arrays.asList(
            Map.of("id", "monthly_revenue", "title", "Keys Used per Month (FY " + fyStartYear + "-" + (fyStartYear + 1)%100 + ")", "xAxis", "month", "yAxis", "revenue",
                "data", barChartDataList
            )
        ));
        
        // Data views
        data.put("dataViews", Arrays.asList(
            Map.of("id", "recent_batches", "title", "Recent Top 5 Batches",
                "data", vendorActivationBatchRepository.findRecentBatchesByVendorId(userId)
                    .stream()
                    .limit(5)
                    .toList()
            ),
            Map.of("id", "top_tenants", "title", "Top 5 Tenants by Revenue",
                "data", tenantActivationRepository.findTopTenantsByVendorId(userId)
                    .stream()
                    .limit(5)
                    .map(row -> {
                        Number revenueNum = (Number) row.get("revenue");
                        Number activationsNum = (Number) row.get("activations");
                        return Map.of(
                            "tenantName",   row.get("tenantName")  != null ? row.get("tenantName")  : "Unknown",
                            "tenantEmail",  row.get("tenantEmail") != null ? row.get("tenantEmail") : "",
                            "revenue",      revenueNum    != null ? revenueNum.doubleValue()    : 0.0,
                            "activations",  activationsNum != null ? activationsNum.longValue() : 0L
                        );
                    })
                    .toList()
            )
        ));
        
        return data;
    }
}

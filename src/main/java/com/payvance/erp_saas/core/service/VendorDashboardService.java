package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.repository.VendorActivationBatchRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    
    /**
     * Get complete dashboard data for Vendor
     * TODO: Implement with actual repository methods
     */
    public Map<String, Object> getDashboardData(Long userId) {
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
        
        // Pie charts data
        data.put("pieCharts", Arrays.asList(
            Map.of("id", "renewal_vs_newsales", "title", "Renewal vs New Sales",
                "data", Arrays.asList(
                    Map.of("name", "Renewal", "value", 40),
                    Map.of("name", "New Sales", "value", 35)
                )
            ),
            Map.of("id", "highest_selling_plans", "title", "Highest Selling Plans",
                "data", Arrays.asList(
                    Map.of("name", "Basic", "value", 40),
                    Map.of("name", "Premium", "value", 35),
                    Map.of("name", "Enterprise", "value", 25)
                )
            )
        ));
        
        // Bar charts data
        data.put("barCharts", Arrays.asList(
            Map.of("id", "monthly_revenue", "title", "Monthly Revenue", "xAxis", "month", "yAxis", "revenue",
                "data", Arrays.asList(
                    Map.of("month", "Apr", "revenue", 10000),
                    Map.of("month", "May", "revenue", 12000),
                    Map.of("month", "Jun", "revenue", 14000),
                    Map.of("month", "Jul", "revenue", 16000),
                    Map.of("month", "Aug", "revenue", 18000),
                    Map.of("month", "Sep", "revenue", 20000),
                    Map.of("month", "Oct", "revenue", 19000),
                    Map.of("month", "Nov", "revenue", 21000),
                    Map.of("month", "Dec", "revenue", 23000),
                    Map.of("month", "Jan", "revenue", 22000),
                    Map.of("month", "Feb", "revenue", 24000),
                    Map.of("month", "Mar", "revenue", 26000)
                )
            )
        ));
        
        // Data views
        data.put("dataViews", Arrays.asList(
            Map.of("id", "recent_batches", "title", "Recent Batches",
                "data", vendorActivationBatchRepository.findRecentBatchesByVendorId(userId)
                    .stream()
                    .limit(5)
                    .map(batch -> Map.of(
                        "name", batch.get("planName") != null ? batch.get("planName") : "Unknown Plan",
                        "value", batch.get("totalActivations") != null && batch.get("status") != null 
                            ? batch.get("totalActivations") + " / " + batch.get("status") 
                            : "0 / Unknown"
                    ))
                    .toList()
            ),
            Map.of("id", "top_tenants", "title", "Top Tenants",
                "data", Arrays.asList(
                    Map.of("name", "Tenant 1", "revenue", "25%"),
                    Map.of("name", "Tenant 2", "revenue", "20%"),
                    Map.of("name", "Tenant 3", "revenue", "15%"),
                    Map.of("name", "Tenant 4", "revenue", "10%"),
                    Map.of("name", "Tenant 5", "revenue", "5%")
                )
            )
        ));
        
        return data;
    }
}

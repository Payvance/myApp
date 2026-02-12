package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.repository.TenantRepository;
import com.payvance.erp_saas.core.repository.VendorRepository;
import com.payvance.erp_saas.core.repository.CaRepository;
import com.payvance.erp_saas.core.repository.PlanRepository;
import com.payvance.erp_saas.core.repository.VendorActivationBatchRepository;
import com.payvance.erp_saas.core.repository.InvoiceRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    
    /**
     * Get complete dashboard data for Superadmin
     */
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        // Get card data values first
        Long totalTenants = tenantRepository.getTotalTenants();
        Long vendorApprovalRequests = vendorRepository.countByStatus("pending_approval");
        Long caApprovalRequests = caRepository.countByStatus("pending_approval");
        Long activePlans = planRepository.countByIsActive(1);
        Long totalRevenueGenerated = getTotalRevenueGenerated();
        Long guestUsers = userRepository.countGuestUsers();
        
        // Cards data - formatted for frontend with null handling
        data.put("cards", Arrays.asList(
            Map.of("id", "total_tenants", "title", "Total Tenants", "value", totalTenants != null ? totalTenants.toString() : "0", "icon", "bi-people", "color", "primary"),
            Map.of("id", "vendor_approval_requests", "title", "Vendor Approval Requests", "value", vendorApprovalRequests != null ? vendorApprovalRequests.toString() : "0", "icon", "bi-hourglass-split", "color", "warning"),
            Map.of("id", "ca_approval_requests", "title", "CA Approval Requests", "value", caApprovalRequests != null ? caApprovalRequests.toString() : "0", "icon", "bi-hourglass-split", "color", "warning"),
            Map.of("id", "active_plans", "title", "Active Plans", "value", activePlans != null ? activePlans.toString() : "0", "icon", "bi-clock-history", "color", "danger"),
            Map.of("id", "total_revenue_generated", "title", "Total Revenue Generated", "value", totalRevenueGenerated != null ? String.format("%,d", totalRevenueGenerated) : "0", "icon", "bi-currency-dollar", "color", "success"),
            Map.of("id", "guest_users", "title", "Guest Users", "value", guestUsers != null ? guestUsers.toString() : "0", "icon", "bi-people", "color", "danger")
        ));
        
        // Pie charts data
        data.put("pieCharts", Arrays.asList(
            Map.of("id", "renewal_vs_newsales", "title", "Renewal vs New Sales", 
                "data", Arrays.asList(
                    Map.of("name", "Renewal", "value", 45),
                    Map.of("name", "New Sales", "value", 30)
                )
            ),
            Map.of("id", "highest_selling_plans", "title", "Highest Selling Plans",
                "data", Arrays.asList(
                    Map.of("name", "Basic", "value", 45),
                    Map.of("name", "Premium", "value", 30),
                    Map.of("name", "Enterprise", "value", 25)
                )
            )
        ));
        
        // Bar charts data
        data.put("barCharts", Arrays.asList(
            Map.of("id", "monthly_revenue", "title", "Monthly Revenue", "xAxis", "month", "yAxis", "revenue",
                "data", Arrays.asList(
                    Map.of("month", "Apr", "revenue", 12000),
                    Map.of("month", "May", "revenue", 15000),
                    Map.of("month", "Jun", "revenue", 18000),
                    Map.of("month", "Jul", "revenue", 22000),
                    Map.of("month", "Aug", "revenue", 25000),
                    Map.of("month", "Sep", "revenue", 28000),
                    Map.of("month", "Oct", "revenue", 26000),
                    Map.of("month", "Nov", "revenue", 30000),
                    Map.of("month", "Dec", "revenue", 32000),
                    Map.of("month", "Jan", "revenue", 29000),
                    Map.of("month", "Feb", "revenue", 31000),
                    Map.of("month", "Mar", "revenue", 35000)
                )
            )
        ));
        
        // Data views
        data.put("dataViews", Arrays.asList(
            Map.of("id", "top5_vendors", "title", "% Revenue From Top 5 Vendors",
                "data", Arrays.asList(
                    Map.of("name", "Vendor 1", "value", "25%"),
                    Map.of("name", "Vendor 2", "value", "20%"),
                    Map.of("name", "Vendor 3", "value", "15%"),
                    Map.of("name", "Vendor 4", "value", "10%"),
                    Map.of("name", "Vendor 5", "value", "5%")
                )
            ),
            Map.of("id", "top5_ca", "title", "% Revenue From Top 5 CA",
                "data", Arrays.asList(
                    Map.of("name", "CA 1", "value", "25%"),
                    Map.of("name", "CA 2", "value", "20%"),
                    Map.of("name", "CA 3", "value", "15%"),
                    Map.of("name", "CA 4", "value", "10%"),
                    Map.of("name", "CA 5", "value", "5%")
                )
            )
        ));
        
        return data;
    }
    
    /**
     * Get total revenue generated from approved batches and paid invoices
     */
    public Long getTotalRevenueGenerated() {
        BigDecimal approvedBatchesSum = vendorActivationBatchRepository.sumCostPriceByStatus("approved");
        BigDecimal paidInvoicesSum = invoiceRepository.sumTotalPayableByStatus("paid");
        
        if (approvedBatchesSum == null && paidInvoicesSum == null) {
            return 0L;
        }
        
        BigDecimal totalRevenue = BigDecimal.ZERO;
        if (approvedBatchesSum != null) {
            totalRevenue = totalRevenue.add(approvedBatchesSum);
        }
        if (paidInvoicesSum != null) {
            totalRevenue = totalRevenue.add(paidInvoicesSum);
        }
        
        return totalRevenue.longValue();
    }
}

package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.repository.TenantUserRoleRepository;
import com.payvance.erp_saas.core.repository.TenantUsageRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
import com.payvance.erp_saas.core.repository.TenantRepository;
import com.payvance.erp_saas.core.repository.SubscriptionRepository;
import com.payvance.erp_saas.core.entity.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for Tenant Dashboard Data
 * TODO: Implement with actual repository methods
 * 
 * @author system
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TenantDashboardService {
    
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final TenantUsageRepository tenantUsageRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final SubscriptionRepository subscriptionRepository;
    
    /**
     * Get complete dashboard data for Tenant
     * TODO: Implement with actual repository methods
     */
    public Map<String, Object> getDashboardData(Long userId, Integer startYear, Integer endYear) {
        Map<String, Object> data = new HashMap<>();
        
        // Find tenantId using single query (role 2 = Tenant)
        Long tenantId = userRepository.findEntityIdByUserAndRole(userId, 2).orElse(null);
        
        // Get active and inactive users count once to avoid duplicate repository calls
        Map<String, Long> activeInactiveCounts = tenantUserRoleRepository.countActiveInactiveUsersByTenantId(tenantId);
        
        // Get 5 most recent users for tenant
        List<Map<String, Object>> recentUsers = tenantUserRoleRepository.findRecentUsersByTenantId(tenantId)
                .stream()
                .limit(5)
                .toList();
        
        // Get tenant status and determine active plan card value
        Optional<Tenant> tenantOpt = tenantRepository.findById(tenantId);
        String tenantStatus = tenantOpt.map(Tenant::getStatus).orElse("unknown");
        
        String activePlanValue;
        if ("active".equalsIgnoreCase(tenantStatus)) {
            // Tenant is active, show active plan
            activePlanValue = subscriptionRepository.findActivePlanNameByTenantId(tenantId).orElse("No Active Plan");
        } else {
            // Tenant is not active, show tenant status
            activePlanValue = tenantStatus.substring(0, 1).toUpperCase() + tenantStatus.substring(1).toLowerCase();
        }
        
        // Get active plan details
        Optional<Map<String, Object>> planDetailsOpt = subscriptionRepository.findActivePlanDetailsByTenantId(tenantId);
        
        // Get card data values first
        Long usersCount = tenantUserRoleRepository.countUsersByTenantId(tenantId);
        Integer companiesCountInt = tenantUsageRepository.findCompaniesCountByTenantId(tenantId);
        Long companiesCount = companiesCountInt != null ? (long) companiesCountInt : 0L;
        Long activeUsers = activeInactiveCounts.get("activeUsers");
        Long inactiveUsers = activeInactiveCounts.get("inactiveUsers");
        
        // Cards data - formatted for frontend
        data.put("cards", Arrays.asList(
            Map.of("id", "users_created", "title", "Users Created", "value", usersCount != null ? usersCount.toString() : "0", "icon", "bi-person-check", "color", "primary"),
            Map.of("id", "companies", "title", "Companies", "value", companiesCount != null ? companiesCount.toString() : "0", "icon", "bi-diagram-3", "color", "success"),
            Map.of("id", "active_plan", "title", "Active Plan", "value", activePlanValue, "icon", "bi-award", "color", "warning"),
            Map.of("id", "active_vs_inactive_users", "title", "Active vs Inactive Users", "value", 
                (activeUsers != null && inactiveUsers != null) ? activeUsers.toString() + "/" + inactiveUsers.toString() : "0/0", "icon", "bi-people", "color", "danger")
        ));
        
        // Pie charts data
        data.put("pieCharts", Arrays.asList(
            Map.of("id", "user_activity", "title", "User Activity",
                "data", Arrays.asList(
                    Map.of("name", "Active", "value", activeInactiveCounts.get("activeUsers") != null ? activeInactiveCounts.get("activeUsers") : 0),
                    Map.of("name", "Inactive", "value", activeInactiveCounts.get("inactiveUsers") != null ? activeInactiveCounts.get("inactiveUsers") : 0)
                )
            )
        ));
        
        // Bar charts data
        data.put("barCharts", Arrays.asList(
            Map.of("id", "user_creation_trend", "title", "User Creation Trend", "xAxis", "month", "yAxis", "users_created",
                "data", getUserCreationTrendData(userId, startYear, endYear)
            )
        ));
        
        // Data views
        data.put("dataViews", Arrays.asList(
            Map.of("id", "recent_users", "title", "Recent Users",
                "data", recentUsers.stream()
                    .map(user -> Map.of(
                        "name", user.get("userName") != null ? user.get("userName") : "Unknown User",
                        "value", user.get("userEmail") != null ? user.get("userEmail") : "No Email"
                    ))
                    .toList()
            ),
            Map.of("id", "plan_details", "title", "Plan Details",
                "data", "trial".equalsIgnoreCase(tenantStatus) ? 
                    Arrays.asList(
                        Map.of("name", "Plan Name", "value", "Trial"),
                        Map.of("name", "Plan Price", "value", "₹0"),
                        Map.of("name", "Start Date", "value", tenantOpt.map(Tenant::getTrialStartAt).map(date -> date.toLocalDate().toString()).orElse("--")),
                        Map.of("name", "End Date", "value", tenantOpt.map(Tenant::getTrialEndAt).map(date -> date.toLocalDate().toString()).orElse("--")),
                        Map.of("name", "Days Left", "value", calculateTrialDaysLeft(tenantOpt))
                    ) : 
                    planDetailsOpt.map(planDetails -> Arrays.asList(
                        Map.of("name", "Plan Name", "value", planDetails.get("planName") != null ? planDetails.get("planName") : "No Plan"),
                        Map.of("name", "Plan Price", "value", planDetails.get("planPrice") != null ? "₹" + planDetails.get("planPrice") : "₹0"),
                        Map.of("name", "Status", "value", planDetails.get("status") != null ? planDetails.get("status") : "--"),
                        Map.of("name", "Start Date", "value", planDetails.get("startAt") != null ? planDetails.get("startAt").toString().split("T")[0] : "--"),
                        Map.of("name", "End Date", "value", planDetails.get("currentPeriodEnd") != null ? planDetails.get("currentPeriodEnd").toString().split("T")[0] : "--")
                    )).orElse(Collections.emptyList())
            )
        ));
        
        return data;
    }
    
    private String calculateTrialDaysLeft(Optional<Tenant> tenantOpt) {
        if (tenantOpt.isEmpty() || tenantOpt.get().getTrialEndAt() == null) {
            return "Trial period not set";
        }
        
        LocalDate today = LocalDate.now();
        LocalDate trialEnd = tenantOpt.get().getTrialEndAt().toLocalDate();
        
        long daysLeft = ChronoUnit.DAYS.between(today, trialEnd);
        
        if (daysLeft < 0) {
            return "Trial expired";
        } else if (daysLeft == 0) {
            return "Trial ends today";
        } else {
            return daysLeft + " days remaining";
        }
    }
    
    /**
     * Get user creation trend data for year range (for main dashboard)
     */
    private List<Map<String, Object>> getUserCreationTrendData(Long userId, Integer startYear, Integer endYear) {
        // Get tenant ID from user ID (role 2 = Tenant)
        Long tenantId = userRepository.findEntityIdByUserAndRole(userId, 2).orElse(null);
        
        if (tenantId == null) {
            return Collections.emptyList();
        }
        
        // Get monthly counts from repository for specified year range
        List<Object[]> monthlyData = tenantUserRoleRepository.countUsersCreatedMonthlyByTenantIdAndYearRange(tenantId, startYear, endYear);
        
        // Convert to bar graph format
        return convertToBarGraphFormatForYearRange(monthlyData, startYear, endYear);
    }
    
    /**
     * Get user creation bar graph data for tenant admin
     */
    public Map<String, Object> getUserCreationBarGraphData(Long userId, Integer startYear, Integer endYear) {
        // Get tenant ID from user ID (role 2 = Tenant)
        Long tenantId = userRepository.findEntityIdByUserAndRole(userId, 2).orElse(null);
        
        if (tenantId == null) {
            return Map.of(
                "id", "user_creation_trend",
                "title", "User Creation Trend - " + startYear + "-" + endYear,
                "xAxis", "month",
                "yAxis", "users_created",
                "data", Collections.emptyList()
            );
        }
        
        // Get monthly counts from repository
        List<Object[]> monthlyData = tenantUserRoleRepository.countUsersCreatedMonthlyByTenantIdAndYearRange(tenantId, startYear, endYear);
        
        // Convert to bar graph format
        List<Map<String, Object>> barData = convertToBarGraphFormatForYearRange(monthlyData, startYear, endYear);
        
        return Map.of(
            "id", "user_creation_trend",
            "title", "User Creation Trend - " + startYear + "-" + endYear,
            "xAxis", "month",
            "yAxis", "users_created",
            "data", barData
        );
    }
    
    /**
     * Convert monthly data to bar graph format (Apr-Mar financial year)
     */
    private List<Map<String, Object>> convertToBarGraphFormat(List<Object[]> monthlyData) {
        // Create map of month -> count
        Map<Integer, Long> monthCounts = monthlyData.stream()
            .collect(Collectors.toMap(
                arr -> (Integer) arr[0],  // month
                arr -> (Long) arr[1]      // count
            ));
        
        // Generate 12 months data (Apr=4 to Mar=3 for financial year)
        String[] months = {"Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Mar"};
        int[] monthNumbers = {4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2, 3};
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            result.add(Map.of(
                "month", months[i],
                "users_created", monthCounts.getOrDefault(monthNumbers[i], 0L)
            ));
        }
        
        return result;
    }
    
    /**
     * Convert monthly data to bar graph format for financial year range (April to March)
     */
    private List<Map<String, Object>> convertToBarGraphFormatForYearRange(List<Object[]> monthlyData, Integer startYear, Integer endYear) {
        // Create map of year-month -> count
        Map<String, Long> yearMonthCounts = monthlyData.stream()
            .collect(Collectors.toMap(
                arr -> (Integer) arr[1] + "-" + String.format("%02d", (Integer) arr[0]),  // year-month
                arr -> (Long) arr[2]      // count
            ));
        
        // Generate financial year combinations (April to March)
        List<Map<String, Object>> result = new ArrayList<>();
        
        // For start year: April to December
        for (int month = 4; month <= 12; month++) {
            String yearMonth = startYear + "-" + String.format("%02d", month);
            String monthName = getMonthName(month);
            
            result.add(Map.of(
                "month", monthName + " " + startYear,
                "users_created", yearMonthCounts.getOrDefault(yearMonth, 0L)
            ));
        }
        
        // For intermediate years: January to December
        for (int year = startYear + 1; year < endYear; year++) {
            for (int month = 1; month <= 12; month++) {
                String yearMonth = year + "-" + String.format("%02d", month);
                String monthName = getMonthName(month);
                
                result.add(Map.of(
                    "month", monthName + " " + year,
                    "users_created", yearMonthCounts.getOrDefault(yearMonth, 0L)
                ));
            }
        }
        
        // For end year: January to March
        for (int month = 1; month <= 3; month++) {
            String yearMonth = endYear + "-" + String.format("%02d", month);
            String monthName = getMonthName(month);
            
            result.add(Map.of(
                "month", monthName + " " + endYear,
                "users_created", yearMonthCounts.getOrDefault(yearMonth, 0L)
            ));
        }
        
        return result;
    }
    
    /**
     * Get month name from month number
     */
    private String getMonthName(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                         "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month - 1];
    }
}

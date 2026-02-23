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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public Map<String, Object> getDashboardData(Long userId) {
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
            Map.of("id", "user_growth", "title", "User Growth", "xAxis", "month", "yAxis", "users",
                "data", Arrays.asList(
                    Map.of("month", "Apr", "users", 45),
                    Map.of("month", "May", "users", 52),
                    Map.of("month", "Jun", "users", 48),
                    Map.of("month", "Jul", "users", 58),
                    Map.of("month", "Aug", "users", 62),
                    Map.of("month", "Sep", "users", 68),
                    Map.of("month", "Oct", "users", 72),
                    Map.of("month", "Nov", "users", 78),
                    Map.of("month", "Dec", "users", 82),
                    Map.of("month", "Jan", "users", 85),
                    Map.of("month", "Feb", "users", 88),
                    Map.of("month", "Mar", "users", 92)
                )
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
                        Map.of("name", "Status", "value", "Trial"),
                        Map.of("name", "Trial Start", "value", tenantOpt.map(Tenant::getTrialStartAt).map(date -> date.toString()).orElse("--")),
                        Map.of("name", "Trial End", "value", tenantOpt.map(Tenant::getTrialEndAt).map(date -> date.toString()).orElse("--")),
                        Map.of("name", "Trial Days Left", "value", calculateTrialDaysLeft(tenantOpt))
                    ) : 
                    planDetailsOpt.map(planDetails -> Arrays.asList(
                        Map.of("name", "Plan Name", "value", planDetails.get("planName") != null ? planDetails.get("planName") : "No Plan"),
                        Map.of("name", "Plan Price", "value", planDetails.get("planPrice") != null ? "₹" + planDetails.get("planPrice") : "₹0"),
                        Map.of("name", "Status", "value", planDetails.get("status") != null ? planDetails.get("status") : "--"),
                        Map.of("name", "Start At", "value", planDetails.get("startAt") != null ? planDetails.get("startAt").toString() : "--"),
                        Map.of("name", "Current Period End", "value", planDetails.get("currentPeriodEnd") != null ? planDetails.get("currentPeriodEnd").toString() : "--")
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
            return String.valueOf(daysLeft);
        }
    }
}

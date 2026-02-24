package com.payvance.erp_saas.core.service;

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

import org.springframework.stereotype.Service;

import com.payvance.erp_saas.core.entity.Tenant;
import com.payvance.erp_saas.core.repository.AddOnRepository;
import com.payvance.erp_saas.core.repository.PlanLimitationRepository;
import com.payvance.erp_saas.core.repository.SubscriptionRepository;
import com.payvance.erp_saas.core.repository.TenantRepository;
import com.payvance.erp_saas.core.repository.TenantUsageRepository;
import com.payvance.erp_saas.core.repository.TenantUserRoleRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
import com.payvance.erp_saas.erp.repository.TallyCompanyRepository;

import lombok.RequiredArgsConstructor;

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
    private final PlanLimitationRepository planLimitationRepository;
    private final AddOnRepository addOnRepository;
    private final TallyCompanyRepository tallyCompanyRepository;
    /**
     * Get complete dashboard data for Tenant
     * TODO: Implement with actual repository methods
     */
    public Map<String, Object> getDashboardData(Long userId, Integer startYear, Integer endYear) {
        Map<String, Object> data = new HashMap<>();

        // 1️⃣ Get Tenant ID
        Long tenantId = userRepository
                .findEntityIdByUserAndRole(userId, 2)
                .orElse(null);

        if (tenantId == null) {
            data.put("cards", Collections.emptyList());
            return data;
        }

        // 2️⃣ Get Tenant
        Optional<Tenant> tenantOpt = tenantRepository.findById(tenantId);
        if (tenantOpt.isEmpty()) {
            data.put("cards", Collections.emptyList());
            return data;
        }

        Tenant tenant = tenantOpt.get();
        String tenantStatus = tenant.getStatus() != null
                ? tenant.getStatus()
                : "inactive";

        // ================================
        // USER COUNTS
        // ================================

        Long totalUsers = tenantUserRoleRepository
                .countUsersByTenantId(tenantId);

        Long activeUsers = tenantUserRoleRepository
                .countActiveUsersByTenantId(tenantId);

        totalUsers = totalUsers != null ? totalUsers : 0L;
        activeUsers = activeUsers != null ? activeUsers : 0L;
        Long inactiveUsers = totalUsers - activeUsers;

        // ================================
        // PLAN + LIMITATIONS + ADDONS
        // ================================

        String planName = "Inactive";
        String allowedCompanyCount = "0";
        String addonsCount = "0";

        if ("trial".equalsIgnoreCase(tenantStatus)) {

            planName = "Trial";

        } else if ("active".equalsIgnoreCase(tenantStatus)) {

            planName = subscriptionRepository
                    .findActivePlanNameByTenantId(tenantId)
                    .orElse("No Active Plan");

        } else {
            planName = capitalize(tenantStatus);
        }

     // ✅ Fetch company count directly from tally_companies table
        long totalCompanies = tallyCompanyRepository.countByTenantId(tenantId);
        String companyCount = String.valueOf(totalCompanies);

        // Get Plan ID only once
        Optional<Long> planIdOpt =
                subscriptionRepository.findActivePlanIdByTenantId(tenantId);

        if (planIdOpt.isPresent()) {

            Long planId = planIdOpt.get();

            // Get addon count
            Long addons =
                    addOnRepository.countActiveAddonsByPlanId(planId);

            addonsCount = addons != null ? addons.toString() : "0";
        }
        
        
        
        // Get 5 most recent users for tenant
        List<Map<String, Object>> recentUsers = tenantUserRoleRepository.findRecentUsersByTenantId(tenantId)
                .stream()
                .limit(5)
                .toList();
        
     // Get active plan details
        Optional<Map<String, Object>> planDetailsOpt = subscriptionRepository.findActivePlanDetailsByTenantId(tenantId);

     // ================================
     // CALCULATIONS USING EXISTING DATA
     // ================================

     double activeUserPercent = totalUsers > 0
             ? (activeUsers * 100.0 / totalUsers)
             : 0;

     double inactiveUserPercent = totalUsers > 0
             ? (inactiveUsers * 100.0 / totalUsers)
             : 0;

     // 🔹 Calculate this month's user creation from bar graph
     List<Map<String, Object>> trendData =
             getUserCreationTrendData(userId, startYear, endYear);

     long thisMonthUsers = 0;
     String currentMonth = getMonthName(LocalDate.now().getMonthValue()) + " " + LocalDate.now().getYear();

     for (Map<String, Object> item : trendData) {
         if (item.get("month").equals(currentMonth)) {
             thisMonthUsers = (Long) item.get("users_created");
             break;
         }
     }

     // 🔹 Calculate today's active users from recentUsers
     long activeToday = recentUsers.stream()
             .filter(u -> Boolean.TRUE.equals(u.get("isActive")))
             .filter(u -> u.get("createdAt") != null &&
                     u.get("createdAt").toString().startsWith(LocalDate.now().toString()))
             .count();

     // 🔹 Calculate inactive users this week
     LocalDate weekStart = LocalDate.now().minusDays(7);

     long inactiveThisWeek = recentUsers.stream()
             .filter(u -> !Boolean.TRUE.equals(u.get("isActive")))
             .filter(u -> {
                 if (u.get("createdAt") == null) return false;
                 LocalDate created = LocalDate.parse(u.get("createdAt").toString().substring(0, 10));
                 return !created.isBefore(weekStart);
             })
             .count();


     // ================================
     // FINAL CARDS
     // ================================

     data.put("cards", Arrays.asList(

             Map.of(
                     "id", "total_users",
                     "title", "Total Users",
                     "value", String.format("%,d", totalUsers),
                     "trend", "+" + thisMonthUsers + " this month",
                     "lastUpdated", "Updated today",
                     "icon", "bi-people",
                     "color", "primary"
             ),

             Map.of(
                     "id", "active_users",
                     "title", "Active Users",
                     "value", String.format("%,d", activeUsers),
                     "percentage", String.format("%.0f%% of Total Users", activeUserPercent),
                     "trend", "+" + activeToday + " Today",
                     "icon", "bi-person-check",
                     "color", "success"
             ),

             Map.of(
                     "id", "inactive_users",
                     "title", "Inactive Users",
                     "value", String.format("%,d", inactiveUsers),
                     "percentage", String.format("%.0f%% of Total Users", inactiveUserPercent),
                     "trend", "-" + inactiveThisWeek + " This Week",
                     "icon", "bi-person-x",
                     "color", "danger"
             ),

             Map.of(
                     "id", "plan",
                     "title", "Plan",
                     "value", planName,
                     "icon", "bi-award",
                     "color", "warning"
             ),

             Map.of(
                     "id", "addons",
                     "title", "Add-ons",
                     "value", addonsCount,
                     "icon", "bi-puzzle-fill",
                     "color", "warning"
             ),

             Map.of(
                     "id", "companies_allowed",
                     "title", "Synced Companies",
                     "value", companyCount,
                     "icon", "bi-buildings-fill",
                     "color", "primary"
             )
     ));
     // Pie charts data
        data.put("pieCharts", Arrays.asList(
                Map.of("id", "user_activity",
                        "title", "User Activity",
                        "data", Arrays.asList(
                                Map.of("name", "Active", "value", activeUsers),
                                Map.of("name", "Inactive", "value", inactiveUsers)
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
        	    Map.of(
        	        "id", "recent_users",
        	        "title", "Recent Top 5 Users",
        	        "data", recentUsers.stream()
        	            .map(user -> Map.of(
        	                "id", user.get("userId"),
        	                "name", user.get("userName") != null ? user.get("userName") : "Unknown User",
        	                "email", user.get("userEmail") != null ? user.get("userEmail") : "No Email",
        	                "createdAt", user.get("createdAt") != null ? user.get("createdAt").toString() : "--",
        	                "status", Boolean.TRUE.equals(user.get("isActive")) ? "Active" : "Inactive",
        	                	"phone", user.get("userPhone") != null ? user.get("userPhone") : "No Phone"	
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
    
    private String capitalize(String tenantStatus) {
		// TODO Auto-generated method stub
		return null;
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

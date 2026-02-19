package com.payvance.erp_saas.core.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.payvance.erp_saas.core.dto.TenantPlanUsageResponse;
import com.payvance.erp_saas.core.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByTenantId(Long tenantId);
    Optional<Subscription> findFirstByTenantIdOrderByCreatedAtDesc(Long tenantId);

    Optional<Subscription> findFirstByTenantIdAndStatusOrderByCreatedAtDesc(Long tenantId, String status);

     @Query("""
        SELECT s
        FROM Subscription s
        JOIN FETCH s.plan
        WHERE s.tenantId = :tenantId
          AND s.status = 'ACTIVE'
    """)
    Optional<Subscription> findActiveSubscription(
            @Param("tenantId") Long tenantId
    );
    
    /*
     * Find active plan name by tenantId
     */
    @Query("""
        SELECT p.name
        FROM Subscription s
        JOIN s.plan p
        WHERE s.tenantId = :tenantId
          AND s.status = 'ACTIVE'
    """)
    Optional<String> findActivePlanNameByTenantId(@Param("tenantId") Long tenantId);
     
    /*
     * Find active plan details by tenantId
     */
    @Query("""
        SELECT new map(
            p.name as planName,
            pp.amount as planPrice,
            s.status as status,
            s.startAt as startAt,
            s.currentPeriodEnd as currentPeriodEnd
        )
        FROM Subscription s
        JOIN s.plan p
        JOIN PlanPrice pp ON pp.plan.id = p.id
        WHERE s.tenantId = :tenantId
          AND s.status = 'ACTIVE'
        """)
    Optional<Map<String, Object>> findActivePlanDetailsByTenantId(@Param("tenantId") Long tenantId);
     
     @Query("""
    	        SELECT new com.payvance.erp_saas.core.dto.TenantPlanUsageResponse(
    	            s.tenantId,
    	            p.id,
    	            s.id,
    	            p.code,
    	            p.name,
    	            s.currentPeriodEnd,
    	            tu.activeUsersCount,
    	            tu.companiesCount,
    	            null
    	        )
    	        FROM Subscription s
    	        JOIN plan p ON s.plan.id = p.id
    	        LEFT JOIN TenantUsage tu ON tu.tenantId = s.tenantId
    	        WHERE s.status = 'ACTIVE' AND s.tenantId = :tenantId
    	    """)

     Page<TenantPlanUsageResponse> findTenantPlanUsageByTenantId(
    	        @Param("tenantId") Long tenantId,
    	        Pageable pageable
    	        );

     
     @Query("""
    		    SELECT s.tenantId
    		    FROM Subscription s
    		    WHERE s.plan.id = :planId
    		""")
    		List<Long> findTenantIdsByPlanId(@Param("planId") Long planId);



}

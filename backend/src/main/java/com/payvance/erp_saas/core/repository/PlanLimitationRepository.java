package com.payvance.erp_saas.core.repository;

import com.payvance.erp_saas.core.entity.Plan;
import com.payvance.erp_saas.core.entity.PlanLimitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for PlanLimitation entity operations.
 *
 * @author Aniket Desai
 */
@Repository
public interface PlanLimitationRepository extends JpaRepository<PlanLimitation, Long> {
    
    /**
     * Find plan limitation by plan
     */
    PlanLimitation findByPlan(Plan plan);
}

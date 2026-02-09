package com.payvance.erp_saas.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.core.entity.Plan;

/**
 * Repository interface for Plan entity operations.
 *
 * @author Aniket Desai
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByCode(String code);
    
    // Find all active plans
    List<Plan> findByIsActive(String isActive);

    List<Plan> findByIsActiveTrue();
    
    /**
     * Count plans by active status (1 for active, 0 for inactive)
     */
    @Query("SELECT COUNT(p) FROM Plan p WHERE p.isActive = :isActive")
    Long countByIsActive(@Param("isActive") Integer isActive);
}

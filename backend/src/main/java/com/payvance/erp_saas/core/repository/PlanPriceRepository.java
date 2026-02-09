package com.payvance.erp_saas.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.core.entity.PlanPrice;

/**
 * Repository interface for PlanPrice entity operations.
 *
 * @author Aniket Desai
 */
@Repository
public interface PlanPriceRepository extends JpaRepository<PlanPrice, Long> {
	Optional<PlanPrice> findByPlan_IdAndIsActive(Long planId, Byte isActive);
}

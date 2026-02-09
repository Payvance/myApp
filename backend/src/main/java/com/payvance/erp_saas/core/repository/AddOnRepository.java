package com.payvance.erp_saas.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.core.entity.AddOn;

/**
 * Repository interface for AddOn entity operations.
 *
 * @author Aniket Desai
 */
@Repository
public interface AddOnRepository extends JpaRepository<AddOn, Long> {
    Optional<AddOn> findByCode(String code);
    
    List<AddOn> findByPlanIdAndStatus(Long planId, String status);
}

package com.payvance.erp_saas.core.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.core.entity.TenantActivation;

@Repository
public interface TenantActivationRepository extends JpaRepository<TenantActivation, Long> {
    boolean existsByTenantIdAndStatus(Long tenantId, String status);
    Optional<TenantActivation> findFirstByTenantIdOrderByCreatedAtDesc(Long tenantId);
}

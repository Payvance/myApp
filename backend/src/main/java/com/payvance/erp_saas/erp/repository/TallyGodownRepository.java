package com.payvance.erp_saas.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payvance.erp_saas.erp.entity.TallyGodown;

public interface TallyGodownRepository extends JpaRepository<TallyGodown, Long> {
    Optional<TallyGodown> findByGuidAndTenantId(String guid, Long tenantId);
    List<TallyGodown> findByTenantIdAndCompanyId(
            Long tenantId,
            String companyId
    );
    
    boolean existsByTenantIdAndCompanyIdAndName(
            Long tenantId,
            String companyId,
            String name
    );
}

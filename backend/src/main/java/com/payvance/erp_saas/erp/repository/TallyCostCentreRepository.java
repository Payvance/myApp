package com.payvance.erp_saas.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payvance.erp_saas.erp.entity.TallyCostCentre;

public interface TallyCostCentreRepository extends JpaRepository<TallyCostCentre, Long> {
    Optional<TallyCostCentre> findByGuidAndTenantId(String guid, Long tenantId);
    List<TallyCostCentre> findByTenantIdAndCompanyId(Long tenantId, String companyId);
    boolean existsByTenantIdAndCompanyIdAndName(
            Long tenantId,
            String companyId,
            String name
    );
}

package com.payvance.erp_saas.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payvance.erp_saas.erp.entity.TallyStockGroup;

public interface TallyStockGroupRepository extends JpaRepository<TallyStockGroup, Long> {
    Optional<TallyStockGroup> findByGuidAndTenantId(String guid, Long tenantId);

    java.util.List<TallyStockGroup> findAllByTenantId(Long tenantId);

    java.util.List<TallyStockGroup> findAllByTenantIdAndCompanyId(Long tenantId, String companyId);
    List<TallyStockGroup> findByTenantIdAndCompanyId(Long tenantId, String companyId);
    boolean existsByTenantIdAndCompanyIdAndName(
            Long tenantId,
            String companyId,
            String name
    );
}

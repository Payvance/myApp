package com.payvance.erp_saas.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payvance.erp_saas.erp.entity.TallyStockCategory;

public interface TallyStockCategoryRepository extends JpaRepository<TallyStockCategory, Long> {
    Optional<TallyStockCategory> findByGuidAndTenantId(String guid, Long tenantId);

    java.util.List<TallyStockCategory> findAllByTenantId(Long tenantId);

    java.util.List<TallyStockCategory> findAllByTenantIdAndCompanyId(Long tenantId, String companyId);
    
    List<TallyStockCategory> findByTenantIdAndCompanyId(Long tenantId, String companyId);
    boolean existsByTenantIdAndCompanyIdAndName(
            Long tenantId,
            String companyId,
            String name
    );
}


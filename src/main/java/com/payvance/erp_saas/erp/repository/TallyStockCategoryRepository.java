package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.TallyStockCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TallyStockCategoryRepository extends JpaRepository<TallyStockCategory, Long> {
    Optional<TallyStockCategory> findByGuidAndTenantId(String guid, Long tenantId);

    java.util.List<TallyStockCategory> findAllByTenantId(Long tenantId);

    java.util.List<TallyStockCategory> findAllByTenantIdAndCompanyId(Long tenantId, String companyId);
}

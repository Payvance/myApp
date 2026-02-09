package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.TallyStockGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TallyStockGroupRepository extends JpaRepository<TallyStockGroup, Long> {
    Optional<TallyStockGroup> findByGuidAndTenantId(String guid, Long tenantId);

    java.util.List<TallyStockGroup> findAllByTenantId(Long tenantId);

    java.util.List<TallyStockGroup> findAllByTenantIdAndCompanyId(Long tenantId, String companyId);
}

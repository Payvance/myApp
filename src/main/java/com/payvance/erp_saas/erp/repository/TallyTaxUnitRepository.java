package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.TallyTaxUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TallyTaxUnitRepository extends JpaRepository<TallyTaxUnit, Long> {
    Optional<TallyTaxUnit> findByGuidAndTenantId(String guid, Long tenantId);
}

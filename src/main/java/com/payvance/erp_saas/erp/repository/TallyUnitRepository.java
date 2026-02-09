package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.TallyUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TallyUnitRepository extends JpaRepository<TallyUnit, Long> {
    Optional<TallyUnit> findByGuidAndTenantId(String guid, Long tenantId);
}

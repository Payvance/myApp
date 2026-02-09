package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.TallyCostCentre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TallyCostCentreRepository extends JpaRepository<TallyCostCentre, Long> {
    Optional<TallyCostCentre> findByGuidAndTenantId(String guid, Long tenantId);
}

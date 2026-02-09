package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.TallyGodown;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TallyGodownRepository extends JpaRepository<TallyGodown, Long> {
    Optional<TallyGodown> findByGuidAndTenantId(String guid, Long tenantId);
}

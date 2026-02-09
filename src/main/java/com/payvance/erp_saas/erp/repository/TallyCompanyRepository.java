package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.TallyCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TallyCompanyRepository extends JpaRepository<TallyCompany, Long> {
    List<TallyCompany> findByTenantId(Long tenantId);

    Optional<TallyCompany> findByGuid(String guid);

    long countByTenantId(Long tenantId);

    Optional<TallyCompany> findByTenantIdAndGuid(Long tenantId, String guid);
}

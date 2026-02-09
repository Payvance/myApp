package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.TallyConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TallyConfigurationRepository extends JpaRepository<TallyConfiguration, Long> {
    Optional<TallyConfiguration> findByTenantId(Long tenantId);
}

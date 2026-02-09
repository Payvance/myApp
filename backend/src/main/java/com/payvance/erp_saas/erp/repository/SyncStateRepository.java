package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.SyncState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SyncStateRepository extends JpaRepository<SyncState, Long> {
    Optional<SyncState> findByTenantIdAndCompanyId(Long tenantId, String companyId);

    java.util.List<SyncState> findAllByTenantId(Long tenantId);
}

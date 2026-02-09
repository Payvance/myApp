package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.Master;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MasterRepository extends JpaRepository<Master, Long> {
    Optional<Master> findByGuidAndTenantId(String guid, Long tenantId);
}

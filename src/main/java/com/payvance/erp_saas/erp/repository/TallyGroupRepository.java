package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.TallyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface TallyGroupRepository extends JpaRepository<TallyGroup, Long> {
    Optional<TallyGroup> findByGuidAndTenantId(String guid, Long tenantId);

    List<TallyGroup> findAllByTenantId(Long tenantId);

    List<TallyGroup> findAllByTenantIdAndCompanyId(Long tenantId, String companyId);

    List<TallyGroup> findByTenantIdAndCompanyIdAndParentName(Long tenantId, String companyId, String parentName);

    List<TallyGroup> findByTenantIdAndCompanyIdAndParentNameIsNull(Long tenantId, String companyId);
    
    List<TallyGroup> findByTenantIdAndCompanyId(Long tenantId, String companyId);
    
    boolean existsByTenantIdAndCompanyIdAndName(
            Long tenantId,
            String companyId,
            String name
    );
}

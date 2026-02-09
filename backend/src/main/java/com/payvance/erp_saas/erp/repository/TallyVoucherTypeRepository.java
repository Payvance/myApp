package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.TallyVoucherType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TallyVoucherTypeRepository extends JpaRepository<TallyVoucherType, Long> {
    Optional<TallyVoucherType> findByGuidAndTenantId(String guid, Long tenantId);

    java.util.List<TallyVoucherType> findAllByTenantId(Long tenantId);

    java.util.List<TallyVoucherType> findAllByTenantIdAndCompanyId(Long tenantId, String companyId);
}

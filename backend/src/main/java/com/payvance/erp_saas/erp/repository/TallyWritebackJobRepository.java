package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.TallyWritebackJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TallyWritebackJobRepository extends JpaRepository<TallyWritebackJob, Long> {

    List<TallyWritebackJob> findByCompanyIdAndStatus(String companyId, String status);

    List<TallyWritebackJob> findByTenantIdAndStatus(Long tenantId, String status);

    List<TallyWritebackJob> findByCompanyIdAndStatusIn(String companyId, List<String> statuses);
}

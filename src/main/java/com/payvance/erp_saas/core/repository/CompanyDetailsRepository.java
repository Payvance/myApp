package com.payvance.erp_saas.core.repository;

import com.payvance.erp_saas.core.entity.CompanyDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyDetailsRepository extends JpaRepository<CompanyDetails, Long> {

    Optional<CompanyDetails> findByTenants_Id(Long tenantId);
}
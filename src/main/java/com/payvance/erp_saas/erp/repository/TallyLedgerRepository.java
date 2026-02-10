package com.payvance.erp_saas.erp.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.erp.entity.TallyLedger;

@Repository
public interface TallyLedgerRepository extends JpaRepository<TallyLedger, Long> {
        Optional<TallyLedger> findByGuidAndTenantId(String guid, Long tenantId);

        Page<TallyLedger> findAllByTenantId(Long tenantId, Pageable pageable);

        Page<TallyLedger> findByTenantIdAndCompanyId(Long tenantId, String companyId, Pageable pageable);
        List<TallyLedger> findByTenantIdAndCompanyId(Long tenantId, String companyId);

        Page<TallyLedger> findByTenantIdAndCompanyIdAndGroupName(Long tenantId, String companyId, String groupName,
                        Pageable pageable);

        @org.springframework.data.jpa.repository.Query("SELECT SUM(l.closingBalance) FROM TallyLedger l WHERE l.tenantId = :tenantId AND l.companyId = :companyId AND l.groupName IN (SELECT g.name FROM TallyGroup g WHERE g.tenantId = :tenantId AND (g.name = :rootGroup OR g.primaryGroup = :rootGroup))")
        BigDecimal sumClosingBalanceByRootGroup(
                        @org.springframework.data.repository.query.Param("tenantId") Long tenantId,
                        @org.springframework.data.repository.query.Param("companyId") String companyId,
                        @org.springframework.data.repository.query.Param("rootGroup") String rootGroup);

        java.util.List<TallyLedger> findByTenantIdAndCompanyIdAndName(Long tenantId, String companyId, String name);

        java.util.List<TallyLedger> findByTenantIdAndName(Long tenantId, String name);
        
        boolean existsByTenantIdAndCompanyIdAndName(
                Long tenantId,
                String companyId,
                String name
        );
}

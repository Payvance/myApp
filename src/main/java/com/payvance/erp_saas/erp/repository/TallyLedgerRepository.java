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

        @org.springframework.data.jpa.repository.Query(nativeQuery = true, value = "WITH RECURSIVE GroupHierarchy AS ( "
                        +
                        "  SELECT name FROM tally_groups WHERE tenant_id = :tenantId AND (name = :rootGroup OR primary_group = :rootGroup) "
                        +
                        "  UNION " +
                        "  SELECT g.name FROM tally_groups g INNER JOIN GroupHierarchy gh ON g.parent_name = gh.name WHERE g.tenant_id = :tenantId "
                        +
                        ") " +
                        "SELECT SUM(l.closing_balance) FROM tally_ledgers l WHERE l.tenant_id = :tenantId AND l.company_id = :companyId AND l.group_name IN (SELECT name FROM GroupHierarchy)")
        BigDecimal sumClosingBalanceByRootGroup(
                        @org.springframework.data.repository.query.Param("tenantId") Long tenantId,
                        @org.springframework.data.repository.query.Param("companyId") String companyId,
                        @org.springframework.data.repository.query.Param("rootGroup") String rootGroup);

        @org.springframework.data.jpa.repository.Query(nativeQuery = true, value = "WITH RECURSIVE GroupHierarchy AS ( "
                        +
                        "  SELECT name FROM tally_groups WHERE tenant_id = :tenantId AND (name = :rootGroup OR primary_group = :rootGroup) "
                        +
                        "  UNION " +
                        "  SELECT g.name FROM tally_groups g INNER JOIN GroupHierarchy gh ON g.parent_name = gh.name WHERE g.tenant_id = :tenantId "
                        +
                        ") " +
                        "SELECT * FROM tally_ledgers l WHERE l.tenant_id = :tenantId AND l.company_id = :companyId AND l.group_name IN (SELECT name FROM GroupHierarchy) ORDER BY ABS(l.closing_balance) DESC")
        Page<TallyLedger> findTopLedgersByRootGroup(
                        @org.springframework.data.repository.query.Param("tenantId") Long tenantId,
                        @org.springframework.data.repository.query.Param("companyId") String companyId,
                        @org.springframework.data.repository.query.Param("rootGroup") String rootGroup,
                        Pageable pageable);

        java.util.List<TallyLedger> findByTenantIdAndCompanyIdAndName(Long tenantId, String companyId, String name);

        java.util.List<TallyLedger> findByTenantIdAndName(Long tenantId, String name);

        boolean existsByTenantIdAndCompanyIdAndName(
                        Long tenantId,
                        String companyId,
                        String name);
}

package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.LedgerEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

        @Query("SELECT le FROM LedgerEntry le JOIN FETCH le.voucher v " +
                        "WHERE le.ledgerName = :ledgerName " +
                        "AND v.tenantId = :tenantId " +
                        "AND (:companyId IS NULL OR v.companyId = :companyId) " +
                        "AND (:fromDate IS NULL OR v.date >= :fromDate) " +
                        "AND (:toDate IS NULL OR v.date <= :toDate) " +
                        "ORDER BY v.date ASC")
        List<LedgerEntry> findStatementEntries(
                        @Param("tenantId") Long tenantId,
                        @Param("companyId") String companyId,
                        @Param("ledgerName") String ledgerName,
                        @Param("fromDate") LocalDate fromDate,
                        @Param("toDate") LocalDate toDate);

        @Query("SELECT le FROM LedgerEntry le JOIN FETCH le.voucher v " +
                        "WHERE le.ledgerName = :ledgerName " +
                        "AND v.tenantId = :tenantId " +
                        "AND (:companyId IS NULL OR v.companyId = :companyId) " +
                        "AND (:fromDate IS NULL OR v.date >= :fromDate) " +
                        "AND (:toDate IS NULL OR v.date <= :toDate) " +
                        "ORDER BY v.date ASC")
        Page<LedgerEntry> findStatementEntriesPageable(
                        @Param("tenantId") Long tenantId,
                        @Param("companyId") String companyId,
                        @Param("ledgerName") String ledgerName,
                        @Param("fromDate") LocalDate fromDate,
                        @Param("toDate") LocalDate toDate,
                        Pageable pageable);
}

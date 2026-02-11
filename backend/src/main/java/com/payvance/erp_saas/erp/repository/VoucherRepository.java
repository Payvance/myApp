package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
        Optional<Voucher> findByGuidAndTenantId(String guid, Long tenantId);

        Optional<Voucher> findByMasterIdAndCompanyIdAndTenantId(Long masterId, String companyId, Long tenantId);

        @org.springframework.data.jpa.repository.Query("SELECT v.voucherType, SUM(v.amount) FROM Voucher v WHERE v.tenantId = :tenantId AND v.companyId = :companyId AND v.date BETWEEN :startDate AND :endDate GROUP BY v.voucherType")
        java.util.List<Object[]> getVoucherSumsByDateRange(
                        @org.springframework.data.repository.query.Param("tenantId") Long tenantId,
                        @org.springframework.data.repository.query.Param("companyId") String companyId,
                        @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate,
                        @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate);

        @org.springframework.data.jpa.repository.Query("SELECT v.voucherType, SUM(v.amount) FROM Voucher v WHERE v.tenantId = :tenantId AND v.date BETWEEN :startDate AND :endDate GROUP BY v.voucherType")
        java.util.List<Object[]> getVoucherSumsByDateRange(
                        @org.springframework.data.repository.query.Param("tenantId") Long tenantId,
                        @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate,
                        @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate);

        @org.springframework.data.jpa.repository.Query("SELECT v FROM Voucher v " +
                        "WHERE v.tenantId = :tenantId " +
                        "AND (:companyId IS NULL OR v.companyId = :companyId) " +
                        "AND v.partyLedgerName = :ledgerName " +
                        "AND (:fromDate IS NULL OR v.date >= :fromDate) " +
                        "AND (:toDate IS NULL OR v.date <= :toDate) " +
                        "ORDER BY v.date DESC")
        java.util.List<Voucher> findVouchersForLedgerStatement(
                        @org.springframework.data.repository.query.Param("tenantId") Long tenantId,
                        @org.springframework.data.repository.query.Param("companyId") String companyId,
                        @org.springframework.data.repository.query.Param("ledgerName") String ledgerName,
                        @org.springframework.data.repository.query.Param("fromDate") java.time.LocalDate fromDate,
                        @org.springframework.data.repository.query.Param("toDate") java.time.LocalDate toDate);

        java.util.List<Voucher> findByTenantIdAndCompanyIdAndMasterIdIn(Long tenantId, String companyId,
                        java.util.List<Long> masterIds);

        java.util.List<Voucher> findByTenantIdAndCompanyIdAndGuidIn(Long tenantId, String companyId, java.util.List<String> guids);
}

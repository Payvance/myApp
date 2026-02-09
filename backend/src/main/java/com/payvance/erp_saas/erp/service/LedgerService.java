package com.payvance.erp_saas.erp.service;

import com.payvance.erp_saas.erp.dto.LedgerStatementDTO;
import com.payvance.erp_saas.erp.entity.TallyLedger;
import com.payvance.erp_saas.erp.entity.Voucher;
import com.payvance.erp_saas.erp.repository.VoucherRepository;
import com.payvance.erp_saas.erp.repository.TallyLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final TallyLedgerRepository ledgerRepository;
    private final VoucherRepository voucherRepository;

    @Transactional(readOnly = true)
    public LedgerStatementDTO getLedgerStatement(Long tenantId, String companyId, String ledgerName,
            LocalDate fromDate, LocalDate toDate) {
        // 1. Get Ledger for Opening Balance
        // We use findAll to find one because guid might not be available, matching by
        // name
        // Ideally we should use GUID, but for now name is the link
        Optional<TallyLedger> ledgerOpt;
        if (companyId != null && !companyId.isEmpty()) {
            ledgerOpt = ledgerRepository.findByTenantIdAndCompanyIdAndName(tenantId, companyId, ledgerName).stream()
                    .findFirst();
        } else {
            // If no companyId, we might get multiple, pick first or error?
            // Mobile app usually sends companyId if selected.
            ledgerOpt = ledgerRepository.findByTenantIdAndName(tenantId, ledgerName).stream().findFirst();
        }

        BigDecimal openingBalance = ledgerOpt.map(TallyLedger::getOpeningBalance).orElse(BigDecimal.ZERO);
        if (openingBalance == null)
            openingBalance = BigDecimal.ZERO;

        // 2. Fetch all vouchers that contain this ledger (already sorted ASC by date)
        List<Voucher> vouchers = voucherRepository.findVouchersForLedgerStatement(
                tenantId, companyId, ledgerName, fromDate, toDate);

        List<LedgerStatementDTO.LedgerStatementEntryDTO> dtos = new ArrayList<>();
        BigDecimal runningBalance = openingBalance;
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (Voucher voucher : vouchers) {
            // Use voucher amount directly - negative means debit, positive means credit
            BigDecimal amount = voucher.getAmount();
            if (amount == null)
                continue;

            // System.err.println("Voucher: " + voucher.getVoucherType() + " Amount: " +
            // amount);
            // Determine debit/credit based on amount sign
            boolean isDebit = amount.compareTo(BigDecimal.ZERO) < 0;
            BigDecimal absAmount = amount.abs();

            if (isDebit) {
                runningBalance = runningBalance.add(absAmount);
                totalDebit = totalDebit.add(absAmount);
            } else {
                runningBalance = runningBalance.subtract(absAmount);
                totalCredit = totalCredit.add(absAmount);
            }

            // Get particulars from partyLedgerName or voucher type
            String particulars = voucher.getPartyLedgerName();
            if (particulars == null || particulars.equals(ledgerName) || particulars.isEmpty()) {
                particulars = voucher.getVoucherType(); // Fallback
            }

            dtos.add(LedgerStatementDTO.LedgerStatementEntryDTO.builder()
                    .voucherId(voucher.getGuid())
                    .voucherNumber(voucher.getVoucherNumber())
                    .voucherType(voucher.getVoucherType())
                    .date(voucher.getDate())
                    .particulars(particulars)
                    .amount(absAmount)
                    .isDebit(isDebit)
                    .runningBalance(runningBalance)
                    .narration(voucher.getNarration())
                    .build());
        }

        // Reverse to show latest first
        Collections.reverse(dtos);

        return LedgerStatementDTO.builder()
                .ledgerName(ledgerName)
                .openingBalance(openingBalance)
                .closingBalance(runningBalance)
                .currentTotalDebit(totalDebit)
                .currentTotalCredit(totalCredit)
                .entries(dtos)
                .build();
    }
}

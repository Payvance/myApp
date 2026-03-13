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
            // Find the ledger entry for the requested ledger
            BigDecimal entryAmount = BigDecimal.ZERO;
            boolean isDebit = false;
            boolean entryFound = false;

            if (voucher.getLedgerEntries() != null) {
                for (com.payvance.erp_saas.erp.entity.LedgerEntry le : voucher.getLedgerEntries()) {
                    if (le.getLedgerName() != null && le.getLedgerName().equals(ledgerName)) {
                        entryAmount = le.getAmount();
                        isDebit = Boolean.TRUE.equals(le.getIsDebit());
                        entryFound = true;
                        break;
                    }
                }
            }

            if (!entryFound) {
                // Fallback to voucher level if no specific ledger entry found (should not
                // happen for valid data)
                BigDecimal voucherAmount = voucher.getAmount();
                if (voucherAmount == null)
                    continue;
                isDebit = voucherAmount.compareTo(BigDecimal.ZERO) < 0;
                entryAmount = voucherAmount.abs();
            }

            if (isDebit) {
                runningBalance = runningBalance.add(entryAmount);
                totalDebit = totalDebit.add(entryAmount);
            } else {
                runningBalance = runningBalance.subtract(entryAmount);
                totalCredit = totalCredit.add(entryAmount);
            }

            // Get particulars from partyLedgerName or voucher type
            String particulars = voucher.getPartyLedgerName();
            if (particulars == null || particulars.equals(ledgerName) || particulars.isEmpty()) {
                particulars = voucher.getVoucherType(); // Fallback
            }

            Long vId = voucher.getId();
            dtos.add(LedgerStatementDTO.LedgerStatementEntryDTO.builder()
                    .voucherId(vId)
                    .voucherNumber(voucher.getVoucherNumber())
                    .voucherType(voucher.getVoucherType())
                    .date(voucher.getDate())
                    .particulars(particulars)
                    .amount(entryAmount)
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

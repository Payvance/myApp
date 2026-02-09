package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.entity.BankDetails;
import com.payvance.erp_saas.core.repository.BankDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
// Service for managing bank details including upserting bank by userId
@Service
@RequiredArgsConstructor
public class BankService {

    private final BankDetailsRepository bankDetailsRepository;

    /**
     * Upsert bank by userId
     */
    @Transactional
    public BankDetails upsertBank(Long userId, BankDetails bank) {
        Optional<BankDetails> existing = bankDetailsRepository.findByUserId(userId);

        if (existing.isPresent()) {
            BankDetails b = existing.get();
            copyBank(bank, b);
            return bankDetailsRepository.save(b);
        } else {
            bank.setUserId(userId);
            return bankDetailsRepository.save(bank);
        }
    }

    private void copyBank(BankDetails source, BankDetails target) {
        target.setBankName(source.getBankName());
        target.setBranchName(source.getBranchName());
        target.setAccountNumber(source.getAccountNumber());
        target.setIfscCode(source.getIfscCode());
    }
}

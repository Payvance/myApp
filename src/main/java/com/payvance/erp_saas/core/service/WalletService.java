package com.payvance.erp_saas.core.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payvance.erp_saas.core.entity.Wallet;
import com.payvance.erp_saas.core.entity.WalletTransaction;
import com.payvance.erp_saas.core.repository.WalletRepository;
import com.payvance.erp_saas.core.repository.WalletTransactionRepository;

@Service
public class WalletService {
	
	@Autowired
    private WalletRepository walletRepository;
	
	@Autowired
	private WalletTransactionRepository walletTransactionRepository;

	/**
	 * Create wallet for owner if not exists
	 */

	public void createWalletIfNotExists(Object owner, Long ownerId) {

        // Dynamically resolve ownerType
        String ownerType = owner.getClass().getSimpleName();
 
        if (!walletRepository.existsByOwnerTypeAndOwnerId(ownerType, ownerId)) {

            Wallet wallet = new Wallet();
            wallet.setOwnerType(ownerType);
            wallet.setOwnerId(ownerId);
            wallet.setBalance(0.0);

            walletRepository.save(wallet);
        }
    }
	
	public Wallet getWalletByOwnerId(Long ownerId) {
        return walletRepository.findByOwnerId(ownerId)
                .orElseThrow(() ->
                        new NoSuchElementException("Wallet not found for ownerId: " + ownerId));
    }

    public List<WalletTransaction> getTransactionsByWalletId(Long walletId) {
        return walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(walletId);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deductBalance(Long ownerId, BigDecimal amount, String referenceType, Long referenceId) {
        Wallet wallet = getWalletByOwnerId(ownerId);
        
        // Subtract from balance (stored as Double)
        double newBalance = wallet.getBalance() - amount.doubleValue();
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        // Record Transaction
        WalletTransaction transaction = WalletTransaction.builder()
                .walletId(wallet.getId())
                .txnType("DEBIT")
                .amount(amount)
                .currency(wallet.getCurrency())
                .referenceType(referenceType)
                .referenceId(referenceId)
                .remarks("Subscription Payment Deduction")
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
        
        walletTransactionRepository.save(transaction);
    }
}

package com.payvance.erp_saas.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.dto.WalletDetailsResponse;
import com.payvance.erp_saas.core.entity.Wallet;
import com.payvance.erp_saas.core.entity.WalletTransaction;
import com.payvance.erp_saas.core.service.WalletService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

	
	private final WalletService walletService;

    @GetMapping("/details")
    public ResponseEntity<?> getWalletDetails(@RequestHeader("X-Tenant-Id") Long ownerId) {
        try {
            Wallet wallet = walletService.getWalletByOwnerId(ownerId);

            List<WalletTransaction> transactions =
                    walletService.getTransactionsByWalletId(wallet.getId());

            WalletDetailsResponse response = WalletDetailsResponse.builder()
                    .wallet(wallet)
                    .transactions(transactions)
                    .build();

            return ResponseEntity.ok(response);

        } catch (NoSuchElementException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", HttpStatus.NOT_FOUND.value());
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}

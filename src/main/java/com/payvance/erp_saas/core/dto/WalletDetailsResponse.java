package com.payvance.erp_saas.core.dto;

import java.util.List;

import com.payvance.erp_saas.core.entity.Wallet;
import com.payvance.erp_saas.core.entity.WalletTransaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletDetailsResponse {
	private Wallet wallet;
    private List<WalletTransaction> transactions;

}

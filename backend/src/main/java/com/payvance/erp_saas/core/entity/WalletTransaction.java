package com.payvance.erp_saas.core.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wallet_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransaction {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(name = "wallet_id", nullable = false)
	    private Long walletId;

	    @Column(name = "txn_type", nullable = false, length = 30)
	    private String txnType;

	    @Column(nullable = false, precision = 14, scale = 2)
	    private BigDecimal amount;

	    @Column(nullable = false, length = 10)
	    private String currency = "INR";

	    @Column(name = "reference_type", length = 60)
	    private String referenceType;

	    @Column(name = "reference_id")
	    private Long referenceId;

	    @Column(length = 191)
	    private String remarks;

	    @Column(name = "created_at")
	    private LocalDateTime createdAt;

	    @Column(name = "updated_at")
	    private LocalDateTime updatedAt;
	
}

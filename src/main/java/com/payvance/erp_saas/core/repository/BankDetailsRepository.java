package com.payvance.erp_saas.core.repository;

import com.payvance.erp_saas.core.entity.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
// Repository for BankDetails entity with method to find by userId
public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {
    Optional<BankDetails> findByUserId(Long userId);
}

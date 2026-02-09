package com.payvance.erp_saas.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.core.entity.VendorDiscount;

@Repository
public interface VendorDiscountRepository extends JpaRepository<VendorDiscount, Long> {
	
	// Fetch the most recent VendorDiscount based on effectiveDate
	Optional<VendorDiscount> findTopByOrderByEffectiveDateDesc();
}

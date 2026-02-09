/**
 * Copyright: Â© 2024 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of Payvance Innovation Pvt. Ltd.
 * Payvance Innovation Pvt. Ltd. retains all title to and intellectual property rights in these materials.
 *
 **/

/**
 *
 * @author           version     date        change description
 * om            	 1.0.0       05-Jan-2026    class created
 *
 **/
package com.payvance.erp_saas.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payvance.erp_saas.core.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    boolean existsByOwnerTypeAndOwnerId(String ownerType, Long ownerId);
    
    Optional<Wallet> findByOwnerId(Long ownerId);
    
}

package com.payvance.erp_saas.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.core.entity.ReferralCode;
import com.payvance.erp_saas.core.entity.ReferralProgram;

@Repository
public interface ReferralCodeRepository extends JpaRepository<ReferralCode, Long> {

    boolean existsByProgramIdAndOwnerId(
            Long programId,
            Long ownerId
    );
    
    Optional<ReferralCode> findByOwnerTypeAndOwnerIdAndStatus(
            String ownerType,
            Long ownerId,
            String status
    );

    Optional<ReferralCode> findByCode(String code);
	
	Optional<ReferralCode> findByOwnerIdAndStatus(Long ownerId, String status);

}

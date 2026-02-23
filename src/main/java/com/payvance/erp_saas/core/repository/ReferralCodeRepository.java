package com.payvance.erp_saas.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
	
	Optional<ReferralCode> findByCodeAndStatus(
            String code,
            String status
    );

    Optional<ReferralCode> findByOwnerId(Long ownerId);

    @Query("SELECT r.code FROM ReferralCode r WHERE r.ownerId = :ownerId AND r.status = 'active'")
    Optional<String> findReferralCodeByOwnerId(@Param("ownerId") Long ownerId);

}

package com.payvance.erp_saas.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.payvance.erp_saas.core.entity.ReferralProgram;
import com.payvance.erp_saas.core.entity.Role;

public interface ReferralProgramRepository extends JpaRepository<ReferralProgram, Long> {

    Optional<ReferralProgram> findByCode(String code);
    List<ReferralProgram> findAllByRoleIdAndStatus(Long roleId, String status);

    Optional<ReferralProgram> findTopByStatusOrderByCreatedAtDesc(String status);
    List<ReferralProgram> findByRoleIdAndStatus(Long roleId, String status);
    @Query("SELECT r FROM ReferralProgram r WHERE r.status = :status AND r.roleId > :roleId")
    List<ReferralProgram> findAllByStatusAndRoleIdGreaterThan(
            @Param("status") String status,
            @Param("roleId") Long roleId
    );
    Optional<ReferralProgram> findTopByStatusAndRoleIdOrderByCreatedAtDesc(
            String status,
            Long roleId
    );

	
}

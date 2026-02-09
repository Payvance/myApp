package com.payvance.erp_saas.core.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.core.entity.EmailOtp;

@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findByEmail(String email);

    void deleteByEmail(String email);

    @Modifying
    @Query("DELETE FROM EmailOtp e WHERE e.expiresAt < :now")
    void deleteExpired(@Param("now") LocalDateTime now);
}
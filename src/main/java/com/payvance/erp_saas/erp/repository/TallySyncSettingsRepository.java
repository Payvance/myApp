package com.payvance.erp_saas.erp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.erp.entity.TallySyncSettings;


@Repository
public interface TallySyncSettingsRepository extends JpaRepository<TallySyncSettings, Long> {
    Optional<TallySyncSettings> findByTenantId(Long tenantId);

}

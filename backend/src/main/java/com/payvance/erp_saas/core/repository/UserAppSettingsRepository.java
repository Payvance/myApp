package com.payvance.erp_saas.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.payvance.erp_saas.core.entity.UserAppSettings;

public interface UserAppSettingsRepository
        extends JpaRepository<UserAppSettings, Long> {
}
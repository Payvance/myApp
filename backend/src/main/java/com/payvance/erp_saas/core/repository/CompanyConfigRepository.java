package com.payvance.erp_saas.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.core.entity.CompanyConfig;

@Repository
public interface CompanyConfigRepository extends JpaRepository<CompanyConfig, Long> {
}
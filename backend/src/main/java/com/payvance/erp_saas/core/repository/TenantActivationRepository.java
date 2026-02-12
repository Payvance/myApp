package com.payvance.erp_saas.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantActivationRepository extends JpaRepository<com.payvance.erp_saas.core.entity.TenantActivation, Long> {

}

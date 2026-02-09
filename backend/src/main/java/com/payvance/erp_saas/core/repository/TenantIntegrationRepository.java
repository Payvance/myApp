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
 * Anjor         	 1.0.0       26-Dec-2025    class created
 *
 **/
package com.payvance.erp_saas.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payvance.erp_saas.core.entity.TenantIntegration;

import java.util.List;

public interface TenantIntegrationRepository extends JpaRepository<TenantIntegration, Long> {

    List<TenantIntegration> findByTenantId(Long tenantId);
}

/**
 * Copyright: © 2024 Payvance Innovation Pvt. Ltd.
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
 * om            	 1.0.0       29-Jan-2026    class created
 *
 **/
package com.payvance.erp_saas.core.repository;

import com.payvance.erp_saas.core.entity.VendorTenants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorTenantsRepository extends JpaRepository<VendorTenants, Long> {
    
    /**
     * Find vendor-tenant relationship by tenant ID and vendor ID
     */
    Optional<VendorTenants> findByTenantIdAndVendorId(Long tenantId, Long vendorId);
    
    /**
     * Find all tenants for a specific vendor
     */
    List<VendorTenants> findByVendorId(Long vendorId);
    
    /**
     * Find all vendors for a specific tenant
     */
    List<VendorTenants> findByTenantId(Long tenantId);
    
    /**
     * Check if vendor-tenant relationship exists
     */
    boolean existsByTenantIdAndVendorId(Long tenantId, Long vendorId);
}

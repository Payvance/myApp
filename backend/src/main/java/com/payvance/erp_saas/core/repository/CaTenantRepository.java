package com.payvance.erp_saas.core.repository;

import com.payvance.erp_saas.core.dto.CATenantListDTO;
import com.payvance.erp_saas.core.dto.TenantManagementListDTO;
import com.payvance.erp_saas.core.entity.CaTenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for CA-Tenant relationship
 * 
 * @author system
 * @version 1.0.0
 */
@Repository
public interface CaTenantRepository extends JpaRepository<CaTenant, Long> {
    
    /**
     * Find CA-Tenant relationship by tenant ID and CA ID
     */
    Optional<CaTenant> findByTenantIdAndCaId(Long tenantId, Long caId);
    
    /**
     * Check if CA-Tenant relationship exists
     */
    boolean existsByTenantIdAndCaId(Long tenantId, Long caId);
    
    /**
     * Find all tenants for a CA with tenant details (pagination)
     */
    @Query("""
        SELECT new com.payvance.erp_saas.core.dto.CATenantListDTO(
            t.id,
            t.name,
            t.email,
            t.phone,
            ct.isView,
            ct.createdAt
        )
        FROM CaTenant ct
        JOIN Tenant t ON ct.tenantId = t.id
        WHERE ct.caId = :caId
        """)
    Page<CATenantListDTO> findTenantsByCaIdWithDetails(@Param("caId") Long caId, Pageable pageable);
    
    /**
     * Find all CAs for a tenant with CA details (pagination)
     */
    @Query("""
        SELECT new com.payvance.erp_saas.core.dto.TenantManagementListDTO(
            c.id,
            u.name,
            u.email,
            u.phone,
            ct.isView,
            ct.createdAt
        )
        FROM CaTenant ct
        JOIN Ca c ON ct.caId = c.userId
        JOIN User u ON c.userId = u.id
        WHERE ct.tenantId = :tenantId
        """)
    Page<TenantManagementListDTO> findCasByTenantIdWithDetails(@Param("tenantId") Long tenantId, Pageable pageable);
}

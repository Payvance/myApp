package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.dto.CATenantListDTO;
import com.payvance.erp_saas.core.entity.CaTenant;
import com.payvance.erp_saas.core.repository.CaTenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service for CA Tenant operations
 * 
 * @author system
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CATenantService {
    
    private final CaTenantRepository caTenantRepository;
    
    /**
     * Get paginated list of tenants for a CA
     */
    public Page<CATenantListDTO> getTenantsForCa(Long caUserId, Pageable pageable) {
        return caTenantRepository.findTenantsByCaIdWithDetails(caUserId, pageable);
    }
    
    /**
     * Update tenant request status (approve/reject)
     * isView: 1 = Approved, 2 = Rejected
     */
    public Map<String, Object> updateTenantStatus(Long caUserId, Long tenantId, Integer isView) {
        Map<String, Object> response = new HashMap<>();
        
        // Validate isView value
        if (isView != 1 && isView != 2) {
            response.put("success", false);
            response.put("message", "Invalid status");
            return response;
        }
        
        // Find CA-Tenant relationship
        Optional<CaTenant> caTenantOpt = caTenantRepository.findByTenantIdAndCaId(tenantId, caUserId);
        
        if (caTenantOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "CA-Tenant relationship not found");
            return response;
        }
        
        // Update status
        CaTenant caTenant = caTenantOpt.get();
        caTenant.setIsView(isView);
        caTenantRepository.save(caTenant);
        
        // Prepare success message
        String statusText = isView == 1 ? "Approved" : "Rejected";
        response.put("success", true);
        response.put("message", "Tenant request " + statusText + " successfully");
        
        return response;
    }
}

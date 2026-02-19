package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.dto.CATenantListDTO;
import com.payvance.erp_saas.core.entity.CaTenant;
import com.payvance.erp_saas.core.entity.Ca;
import com.payvance.erp_saas.core.entity.Tenant;
import com.payvance.erp_saas.core.entity.User;
import com.payvance.erp_saas.core.repository.CaTenantRepository;
import com.payvance.erp_saas.core.repository.CaRepository;
import com.payvance.erp_saas.core.repository.TenantRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
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
    private final CaRepository caRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    
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
        
        // Get tenant and CA details for email
        Optional<Tenant> tenantOpt = tenantRepository.findById(tenantId);
        Optional<User> caUserOpt = userRepository.findById(caUserId);
        
        if (tenantOpt.isPresent() && caUserOpt.isPresent()) {
            String tenantEmail = tenantOpt.map(Tenant::getEmail).orElse("");
            String tenantName = tenantOpt.map(t -> t.getName() != null ? t.getName() : "").orElse("");
            String caEmail = caUserOpt.map(User::getEmail).orElse("");
            String caName = caUserOpt.map(User::getName).orElse("");
            
            // Send email notifications for approval/rejection
            emailService.sendTenantStatusUpdateEmails(tenantEmail, tenantName, caEmail, caName, isView);
        }
        
        // Prepare success message
        String statusText = isView == 1 ? "Approved" : "Rejected";
        response.put("success", true);
        response.put("message", "Tenant request " + statusText + " successfully");
        
        return response;
    }
}

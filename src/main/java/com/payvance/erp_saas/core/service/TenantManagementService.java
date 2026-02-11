package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.entity.Ca;
import com.payvance.erp_saas.core.entity.CaTenant;
import com.payvance.erp_saas.core.entity.ReferralCode;
import com.payvance.erp_saas.core.repository.CaRepository;
import com.payvance.erp_saas.core.repository.CaTenantRepository;
import com.payvance.erp_saas.core.repository.ReferralCodeRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service for Tenant Management operations
 * 
 * @author system
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TenantManagementService {
    
    private final UserRepository userRepository;
    private final CaRepository caRepository;
    private final ReferralCodeRepository referralCodeRepository;
    private final CaTenantRepository caTenantRepository;
    
    /**
     * Process tenant management request
     */
    public Map<String, Object> processTenantRequest(Long userId, String caNo, String referenceCode) {
        Map<String, Object> response = new HashMap<>();
        
        // Find tenant ID using existing method (role 2 = Tenant)
        Optional<Long> tenantIdOpt = userRepository.findEntityIdByUserAndRole(userId, 2);
        
        // Get CA ID using caNo and referenceCode
        Long caId = findCaIdByCanoAndRefCode(caNo, referenceCode);
        
        // Check if CA ID matching failed
        if (caId == null) {
            response.put("success", false);
            response.put("message", "Wrong CA credentials");
            return response;
        }
        
        if (tenantIdOpt.isPresent()) {
            Long tenantId = tenantIdOpt.get();
            
            // Check if CA-Tenant relationship already exists
            if (caTenantRepository.existsByTenantIdAndCaId(tenantId, caId)) {
                response.put("success", false);
                response.put("message", "already requested");
                return response;
            }
            
            // Add CA-Tenant relationship if CA ID is found and entry doesn't exist
            if (caId != null) {
                addCaTenantIfNotExists(tenantId, caId);
            }
            
            response.put("success", true);
            response.put("message", "Request Sent to CA");
        } else {
            response.put("success", false);
            response.put("message", "Tenant not found for this user");
        }
        
        return response;
    }
    
    /**
     * Add CA-Tenant relationship if not exists
     */
    private void addCaTenantIfNotExists(Long tenantId, Long caId) {
        if (!caTenantRepository.existsByTenantIdAndCaId(tenantId, caId)) {
            CaTenant caTenant = new CaTenant();
            caTenant.setTenantId(tenantId);
            caTenant.setCaId(caId); // This stores ca.user_id (which is the caId we validated)
            caTenant.setIsView(0); // Set to 0 for new requests
            
            caTenantRepository.save(caTenant);
        }
    }
    
    /**
     * Find CA ID by CA number and reference code
     */
    private Long findCaIdByCanoAndRefCode(String caNo, String referenceCode) {
        // Find CA by icai_member_no (using caNo from payload)
        Optional<Ca> caOpt = caRepository.findByIcaiMemberNo(caNo);
        
        // Find referral code by code
        Optional<ReferralCode> referralCodeOpt = referralCodeRepository.findByCode(referenceCode);
        
        if (caOpt.isPresent() && referralCodeOpt.isPresent()) {
            Long caUserId = caOpt.get().getUserId(); // Use user_id column from CA table
            Long ownerId = referralCodeOpt.get().getOwnerId();
            
            // Match user_id from CA table with owner_id from referral_code table
            if (caUserId.equals(ownerId)) {
                return caUserId; // Return ca.user_id (not ca.id)
            }
        }
        
        return null; // No match found
    }
}

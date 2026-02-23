package com.payvance.erp_saas.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.payvance.erp_saas.core.dto.ActivationKeyListDTO;
import com.payvance.erp_saas.core.entity.ActivationKey;
import com.payvance.erp_saas.core.repository.ActivationKeyRepository;
import com.payvance.erp_saas.core.repository.TenantRepository;

@Service
public class ActivationKeyService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ActivationKeyService.class);

    private final ActivationKeyRepository repository;
	
	@Autowired
	private TenantRepository tenantRepository;

    // Constructor for injecting ActivationKeyRepository
    public ActivationKeyService(ActivationKeyRepository repository) {
        this.repository = repository;
    }

	/*
	 * * Retrieves a paginated list of all activation keys.
	 */
    public Page<ActivationKeyListDTO> getAllKeys(Pageable pageable) {
        return repository.findAllKeys(pageable);
    }
    
    /*
     * Retrieves an activation key by its ID, including tenant name if redeemed.
     */
    public ActivationKey getById(Long id) {

        ActivationKey key = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activation key not found"));

        if (key.getRedeemedTenantId() != null) {
            tenantRepository.findTenantNameById(key.getRedeemedTenantId())
                    .ifPresent(key::setRedeemedTenantName);
        }

        return key;
    }

    
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final java.util.Random random = new java.security.SecureRandom();

    @org.springframework.transaction.annotation.Transactional
    public java.util.Map<String, String> generateSystemKeyForTenant(Long tenantId, String email, String phone) {
        logger.info("[DEBUG] Checking if tenant {} already has a key", tenantId);
        // Check if any activation key already exists for this tenant
        if (repository.existsByRedeemedTenantId(tenantId)) {
            logger.warn("[DEBUG] Tenant {} already has a key", tenantId);
            throw new RuntimeException("Tenant already has an activation key assigned. New key generation prohibited.");
        }
        logger.info("[DEBUG] No key found for tenant {}, proceeding with generation", tenantId);

        String plainCode = "VND-" + generateRandomString(4) + "-" + generateRandomString(4) + "-"
                + generateRandomString(4);

        String hash = dummyHash(plainCode);
        
        ActivationKey key = new ActivationKey();
        key.setVendorBatchId(null); // Explicitly null
        key.setActivationCodeHash(hash);
        key.setPlainCodeLast4(plainCode.substring(plainCode.length() - 4));
        key.setStatus(ActivationKey.Status.ISSUED);
        key.setIssuedToEmail(email);
        key.setIssuedToPhone(phone);
        key.setRedeemedTenantId(tenantId);
        key.setExpiresAt(java.time.LocalDateTime.now().plusYears(1)); // Default 1 year expiry

        repository.save(key);
        
        java.util.Map<String, String> result = new java.util.HashMap<>();
        result.put("plainKey", plainCode);
        result.put("hash", hash);
        result.put("isNew", "true");
        return result;
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private String dummyHash(String input) {
         return org.springframework.security.crypto.bcrypt.BCrypt.hashpw(input, org.springframework.security.crypto.bcrypt.BCrypt.gensalt());
    }

}

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
	
	@Autowired
    private ActivationKeyRepository repository;
	
	@Autowired
	private TenantRepository tenantRepository;

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

}

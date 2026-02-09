package com.payvance.erp_saas.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.entity.ActivationKey;
import com.payvance.erp_saas.core.service.ActivationKeyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/activation-keys")
@RequiredArgsConstructor
public class ActivationKeyController {
	
	private final ActivationKeyService activationKeyService;

	/**
	 * Endpoint to get Activation Key by ID.
	 *
	 * @param id the activation key id
	 * @return the activation key entity
	 */
    @GetMapping("/{id}")
    public ResponseEntity<ActivationKey> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                activationKeyService.getById(id)
        );
    }

}

package com.payvance.erp_saas.erp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.erp.dto.TallySyncSettingsRequest;
import com.payvance.erp_saas.erp.entity.TallySyncSettings;
import com.payvance.erp_saas.erp.service.TallySyncSettingsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tally/sync-settings")
@RequiredArgsConstructor
public class TallySyncSettingsController {

	private final TallySyncSettingsService service;
	private final com.payvance.erp_saas.core.service.TenantService tenantService;

	@PostMapping("/save")
	public ResponseEntity<TallySyncSettings> save(@RequestBody TallySyncSettingsRequest request) {
		return ResponseEntity.ok(service.saveOrUpdate(request));
	}

	@org.springframework.web.bind.annotation.GetMapping
	public ResponseEntity<?> getSettings() {
		try {
			com.payvance.erp_saas.erp.entity.TallySyncSettings settings = service.getSettings();
			Long tenantId = com.payvance.erp_saas.erp.security.TenantContext.getCurrentTenant();
			java.util.Map<String, Object> licenseStatus = tenantService.getLicenseStatus(tenantId);

			// Convert entity to Map to add licenseStatus field
			com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
			mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
			java.util.Map<String, Object> response = mapper.convertValue(settings, new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {});
			response.put("licenseStatus", licenseStatus);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}
}

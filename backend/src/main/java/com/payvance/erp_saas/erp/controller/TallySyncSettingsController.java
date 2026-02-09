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

	@PostMapping("/save")
	public ResponseEntity<TallySyncSettings> save(@RequestBody TallySyncSettingsRequest request) {
		return ResponseEntity.ok(service.saveOrUpdate(request));
	}

	@org.springframework.web.bind.annotation.GetMapping
	public ResponseEntity<?> getSettings() {
		try {
			return ResponseEntity.ok(service.getSettings());
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}
}

package com.payvance.erp_saas.core.service;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payvance.erp_saas.core.repository.GstRateRepository;

@Service
public class GstRateService {
	
	@Autowired
    private GstRateRepository gstRateRepository;

	
	/*
	 * Get the latest GST rate and its effective date.
	 */
	public Map<String, Object> getLatestGstRate() {
        return gstRateRepository
                .findLatestRateAndDate(LocalDate.now())
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("GST rate not configured"));
    }

}

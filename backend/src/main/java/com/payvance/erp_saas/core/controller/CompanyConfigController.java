package com.payvance.erp_saas.core.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.service.CompanyConfigService;

@RestController
@RequestMapping("/api/company-config")
public class CompanyConfigController {
	private final CompanyConfigService companyConfigService;

    public CompanyConfigController(CompanyConfigService companyConfigService) {
        this.companyConfigService = companyConfigService;
    }

    /**
     * Fetch company config as code-value list
     */
    @GetMapping("/details/list")
    public List<Map<String, Object>> getCompanyConfig() {
        return companyConfigService.getCompanyConfigCodeValue();
    }

}

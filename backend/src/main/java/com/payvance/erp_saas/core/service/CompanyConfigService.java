package com.payvance.erp_saas.core.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.payvance.erp_saas.core.entity.CompanyConfig;
import com.payvance.erp_saas.core.repository.CompanyConfigRepository;

@Service
public class CompanyConfigService {
	
	private final CompanyConfigRepository repository;

    public CompanyConfigService(CompanyConfigRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns company config in code-value format
     */
    public List<Map<String, Object>> getCompanyConfigCodeValue() {

        CompanyConfig config = repository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Company config not found")
                );

        List<Map<String, Object>> response = new ArrayList<>();

        response.add(codeValue("companyName", config.getCompanyName()));
        response.add(codeValue("email", config.getEmail()));
        response.add(codeValue("phone", config.getPhone()));
        response.add(codeValue("address", config.getAddress()));

        return response;
    }

    private Map<String, Object> codeValue(String code, Object value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("code", code);
        map.put("value", value);
        return map;
    }

}

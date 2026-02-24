package com.payvance.erp_saas.erp.service;

import com.payvance.erp_saas.erp.dto.HsnSacDTO;
import com.payvance.erp_saas.erp.entity.HsnSac;
import com.payvance.erp_saas.erp.repository.HsnSacRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HsnSacService {

    private final HsnSacRepository hsnSacRepository;

    public String getDescriptionByCode(String code) {
        return hsnSacRepository.findById(code)
                .map(HsnSac::getDescription)
                .orElse("Description not found for code: " + code);
    }

    public List<HsnSacDTO> getHsnSacByType(Integer type) {
        return hsnSacRepository.findByHsnSac(type).stream()
                .map(hsn -> new HsnSacDTO(hsn.getCode(), hsn.getDescription(), hsn.getHsnSac()))
                .collect(Collectors.toList());
    }

    public List<HsnSacDTO> getAllHsnSac() {
        return hsnSacRepository.findAll().stream()
                .map(hsn -> new HsnSacDTO(hsn.getCode(), hsn.getDescription(), hsn.getHsnSac()))
                .collect(Collectors.toList());
    }
}

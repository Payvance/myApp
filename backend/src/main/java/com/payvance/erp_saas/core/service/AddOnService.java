package com.payvance.erp_saas.core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payvance.erp_saas.core.dto.AddOnDto;
import com.payvance.erp_saas.core.dto.AddOnResponseDto;
import com.payvance.erp_saas.core.entity.AddOn;
import com.payvance.erp_saas.core.entity.Plan;
import com.payvance.erp_saas.core.repository.AddOnRepository;
import com.payvance.erp_saas.core.repository.PlanRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service to handle business logic for Add-ons.
 *
 * @author Aniket Desai
 */
@Service
@RequiredArgsConstructor
public class AddOnService {

    private final AddOnRepository addOnRepository;
    private final PlanRepository planRepository;

    /**
     * Creates a new Add-on.
     *
     * @param addOnDto the add-on data transfer object
     * @return the created add-on dto
     */
    @Transactional
    public AddOnDto createAddOn(AddOnDto addOnDto) {
        if (addOnRepository.findByCode(addOnDto.getCode()).isPresent()) {
            throw new IllegalArgumentException("Add-on with code " + addOnDto.getCode() + " already exists.");
        }
        

        AddOn addOn = new AddOn();
        updateEntityFromDto(addOn, addOnDto);

        AddOn savedAddOn = addOnRepository.save(addOn);
        return mapToDto(savedAddOn);
    }

    /**
     * Updates an existing Add-on.
     *
     * @param id       the add-on id
     * @param addOnDto the updated add-on data
     * @return the updated add-on dto
     */
    @Transactional
    public AddOnDto updateAddOn(Long id, AddOnDto addOnDto) {
        AddOn addOn = addOnRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Add-on not found with id: " + id));

        updateEntityFromDto(addOn, addOnDto);
        // Ensure code uniqueness check if code is being changed could be added here,
        // but typically code is immutable or unique constraint will catch it.
        // Assuming code update is allowed but needs care.
        // For simplicity, we just update fields.
        // If code is strictly unique and immutable, we might skip updating it or check
        // existence.

        AddOn savedAddOn = addOnRepository.save(addOn);
        return mapToDto(savedAddOn);
    }

    /**
     * Retrieves an Add-on by its ID.
     *
     * @param id the add-on id
     * @return the add-on dto
     */
    public AddOnDto getAddOn(Long id) {
        AddOn addOn = addOnRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Add-on not found with id: " + id));
        return mapToDto(addOn);
    }

    /**
     * Retrieves all Add-ons.
     *
     * @return list of add-on dtos
     */
    public List<AddOnDto> getAllAddOns() {
        return addOnRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    
    private void updateEntityFromDto(AddOn addOn, AddOnDto dto) {
        addOn.setCode(dto.getCode());
        addOn.setName(dto.getName());
        addOn.setCurrency(dto.getCurrency());
        addOn.setUnit(dto.getUnit());
        addOn.setUnitPrice(dto.getUnitPrice());
        addOn.setStatus(dto.getStatus());
		Plan plan = planRepository.findById(dto.getPlanId())
				.orElseThrow(() -> new IllegalArgumentException("Plan not found with id: " + dto.getPlanId()));
		addOn.setPlan(plan);
       
    }

    private AddOnDto mapToDto(AddOn addOn) {
        AddOnDto dto = new AddOnDto();
        dto.setId(addOn.getId());
        dto.setCode(addOn.getCode());
        dto.setName(addOn.getName());
        dto.setCurrency(addOn.getCurrency());
        dto.setUnit(addOn.getUnit());
        dto.setUnitPrice(addOn.getUnitPrice());
        dto.setStatus(addOn.getStatus());
        dto.setPlanId(addOn.getPlan().getId());
        return dto;
    }
    
    
    public List<AddOnDto> getAddOnsByPlanId(Long planId) {

        return addOnRepository
                .findByPlanIdAndStatus(planId, "active")
                .stream()
                .map(this::mapToDto)
                .toList();
    }
}

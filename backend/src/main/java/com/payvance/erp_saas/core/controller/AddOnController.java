package com.payvance.erp_saas.core.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.dto.AddOnByPlanRequestDto;
import com.payvance.erp_saas.core.dto.AddOnDto;
import com.payvance.erp_saas.core.entity.AddOn;
import com.payvance.erp_saas.core.service.AddOnService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for managing Add-ons.
 *
 * @author Aniket Desai
 */
@RestController
@RequestMapping("/api/admin/addons")
@RequiredArgsConstructor
public class AddOnController {

    private final AddOnService addOnService;

    /**
     * Endpoint to create a new Add-on.
     *
     * @param addOnDto the add-on data
     * @return the created add-on
     */
    @PostMapping
    public ResponseEntity<AddOnDto> createAddOn(@Valid @RequestBody AddOnDto addOnDto) {
        return ResponseEntity.ok(addOnService.createAddOn(addOnDto));
    }

    /**
     * Endpoint to update an existing Add-on.
     *
     * @param id       the add-on id
     * @param addOnDto the updated add-on data
     * @return the updated add-on
     */
    @PutMapping("/{id}")
    public ResponseEntity<AddOnDto> updateAddOn(@PathVariable Long id, @Valid @RequestBody AddOnDto addOnDto) {
        return ResponseEntity.ok(addOnService.updateAddOn(id, addOnDto));
    }

    /**
     * Endpoint to get an Add-on by ID.
     *
     * @param id the add-on id
     * @return the add-on dto
     */
    @GetMapping("/{id}")
    public ResponseEntity<AddOnDto> getAddOn(@PathVariable Long id) {
        return ResponseEntity.ok(addOnService.getAddOn(id));
    }

    /**
     * Endpoint to get all Add-ons.
     *
     * @return list of all add-ons
     */
    @GetMapping
    public ResponseEntity<List<AddOnDto>> getAllAddOns() {
        return ResponseEntity.ok(addOnService.getAllAddOns());
    }
    
    
    /*
     * * Endpoint to get Add-ons by Plan ID.
     * * @param request the request containing the plan ID
     * * @return list of add-ons associated with the plan
     */
    @PostMapping("/by-plan")
    public ResponseEntity<List<AddOnDto>> getAddOnsByPlan(
            @Valid @RequestBody AddOnByPlanRequestDto request) {

        return ResponseEntity.ok(
                addOnService.getAddOnsByPlanId(request.getPlanId())
        );
    }
}

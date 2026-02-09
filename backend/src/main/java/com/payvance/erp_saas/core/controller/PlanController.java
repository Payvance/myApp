package com.payvance.erp_saas.core.controller;

import com.payvance.erp_saas.core.dto.PlanDto;
import com.payvance.erp_saas.core.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Plans.
 *
 * @author Aniket Desai
 */
@RestController
@RequestMapping("/api/admin/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    /**
     * Endpoint to create a new Plan.
     *
     * @param planDto the plan data
     * @return the created plan
     */
    @PostMapping
    public ResponseEntity<PlanDto> createPlan(@Valid @RequestBody PlanDto planDto) {
        return ResponseEntity.ok(planService.createPlan(planDto));
    }

    /**
     * Endpoint to update an existing Plan.
     *
     * @param id      the plan id
     * @param planDto the updated plan data
     * @return the updated plan
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlanDto> updatePlan(@PathVariable Long id, @Valid @RequestBody PlanDto planDto) {
        return ResponseEntity.ok(planService.updatePlan(id, planDto));
    }

    /**
     * Endpoint to get a Plan by ID.
     *
     * @param id the plan id
     * @return the plan dto
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlanDto> getPlan(@PathVariable Long id) {
        return ResponseEntity.ok(planService.getPlan(id));
    }

    /**
     * Endpoint to get all Plans.
     *
     * @return list of all plans
     */
    @GetMapping
    public ResponseEntity<List<PlanDto>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }
}

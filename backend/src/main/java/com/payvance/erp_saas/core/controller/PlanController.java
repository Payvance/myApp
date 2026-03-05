package com.payvance.erp_saas.core.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.dto.PlanDto;
import com.payvance.erp_saas.core.service.PlanService;
import com.payvance.erp_saas.core.service.SubscriptionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
    private final SubscriptionService subscriptionService;

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
    
    @GetMapping("/active/plan")
    public ResponseEntity<?> getActivePlan(
            @RequestHeader("X-Tenant-Id") Long tenantId) {

        return ResponseEntity.ok(subscriptionService.getActivePlanDetails(tenantId));
    }
}

package com.payvance.erp_saas.core.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow.Subscription;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payvance.erp_saas.core.dto.PlanDto;
import com.payvance.erp_saas.core.dto.PlanLimitationDto;
import com.payvance.erp_saas.core.dto.PlanPriceDto;
import com.payvance.erp_saas.core.entity.Plan;
import com.payvance.erp_saas.core.entity.PlanLimitation;
import com.payvance.erp_saas.core.entity.PlanPrice;
import com.payvance.erp_saas.core.repository.PlanRepository;
import com.payvance.erp_saas.core.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service to handle business logic for Plans.
 *
 * @author Aniket Desai
 */
@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    /**
     * Creates a new Plan.
     *
     * @param planDto the plan data transfer object
     * @return the created plan dto
     */
    @Transactional
    public PlanDto createPlan(PlanDto planDto) {
        if (planRepository.findByCode(planDto.getCode()).isPresent()) {
            throw new IllegalArgumentException("Plan with code " + planDto.getCode() + " already exists.");
        }

        Plan plan = new Plan();
        plan.setCode(planDto.getCode());
        plan.setName(planDto.getName());
        plan.setIsActive(planDto.getIsActive());
        plan.setIsSeparateDb(planDto.getIsSeparateDb());

        PlanLimitation planLimitation = new PlanLimitation();
        updatePlanLimitationFromDto(planLimitation, planDto.getPlanLimitation());
        planLimitation.setPlan(plan);
        plan.setPlanLimitation(planLimitation);

        PlanPrice planPrice = new PlanPrice();
        updatePlanPriceFromDto(planPrice, planDto.getPlanPrice());
        planPrice.setPlan(plan);
        plan.setPlanPrice(planPrice);
        planPrice.setDuration(planDto.getPlanPrice().getDuration());

        Plan savedPlan = planRepository.save(plan);
        return mapToDto(savedPlan);
    }

    /**
     * Updates an existing Plan.
     *
     * @param id      the plan id
     * @param planDto the updated plan data
     * @return the updated plan dto
     */
    @Transactional
    public PlanDto updatePlan(Long id, PlanDto planDto) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found with id: " + id));

        plan.setName(planDto.getName());
        plan.setIsActive(planDto.getIsActive());
        plan.setIsSeparateDb(planDto.getIsSeparateDb());

        updatePlanLimitationFromDto(plan.getPlanLimitation(), planDto.getPlanLimitation());

        // Update price - assuming we just update the existing one
        updatePlanPriceFromDto(plan.getPlanPrice(), planDto.getPlanPrice());

        Plan savedPlan = planRepository.save(plan);
        return mapToDto(savedPlan);
    }

    /**
     * Retrieves a Plan by its ID.
     *
     * @param id the plan id
     * @return the plan dto
     */
    public PlanDto getPlan(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found with id: " + id));
        return mapToDto(plan);
    }

    /**
     * Retrieves all Plans.
     *
     * @return list of plan dtos
     */
    public List<PlanDto> getAllPlans() {
        return planRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private void updatePlanLimitationFromDto(PlanLimitation limitation, PlanLimitationDto dto) {
        limitation.setAllowedUserCount(dto.getAllowedUserCount());
        limitation.setAllowedCompanyCount(dto.getAllowedCompanyCount());
        limitation.setAllowedUserCountTill(dto.getAllowedUserCountTill());
        limitation.setAllowedCompanyCountTill(dto.getAllowedCompanyCountTill());
    }

    private void updatePlanPriceFromDto(PlanPrice price, PlanPriceDto dto) {
        price.setBillingPeriod(dto.getBillingPeriod());
        price.setCurrency(dto.getCurrency());
        price.setAmount(dto.getAmount());
        price.setIsActive(dto.getIsActive());
        price.setDuration(dto.getDuration());
    }

    private PlanDto mapToDto(Plan plan) {
        PlanDto dto = new PlanDto();
        dto.setId(plan.getId());
        dto.setCode(plan.getCode());
        dto.setName(plan.getName());
        dto.setIsActive(plan.getIsActive());
        dto.setIsSeparateDb(plan.getIsSeparateDb());
        
        List<Long> tenantIds =
        	    subscriptionRepository.findTenantIdsByPlanId(plan.getId());

        	dto.setTenantIds(tenantIds);


        PlanLimitationDto limDto = new PlanLimitationDto();
        limDto.setId(plan.getPlanLimitation().getId());
        limDto.setAllowedUserCount(plan.getPlanLimitation().getAllowedUserCount());
        limDto.setAllowedCompanyCount(plan.getPlanLimitation().getAllowedCompanyCount());
        limDto.setAllowedUserCountTill(plan.getPlanLimitation().getAllowedUserCountTill());
        limDto.setAllowedCompanyCountTill(plan.getPlanLimitation().getAllowedCompanyCountTill());
        dto.setPlanLimitation(limDto);

        PlanPriceDto priceDto = new PlanPriceDto();
        priceDto.setId(plan.getPlanPrice().getId());
        priceDto.setBillingPeriod(plan.getPlanPrice().getBillingPeriod());
        priceDto.setCurrency(plan.getPlanPrice().getCurrency());
        priceDto.setAmount(plan.getPlanPrice().getAmount());
        priceDto.setIsActive(plan.getPlanPrice().getIsActive());
        priceDto.setDuration(plan.getPlanPrice().getDuration());
        dto.setPlanPrice(priceDto);

        return dto;
    }
    
    
	/*
	 * * Retrieves active plans for dropdown.
	 *
	 * @return list of maps with plan code and value
	 */
    public List<Map<String, Object>> getActivePlansForDropdown() {

        List<Plan> plans = planRepository.findByIsActive("1");

        List<Map<String, Object>> response = new ArrayList<>();

        for (Plan plan : plans) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("code", plan.getId());
            map.put("value", plan.getCode() + "-" + plan.getName());
            response.add(map);
        }

        return response;
    }

}

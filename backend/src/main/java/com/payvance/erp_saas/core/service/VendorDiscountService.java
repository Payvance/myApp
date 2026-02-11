package com.payvance.erp_saas.core.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payvance.erp_saas.core.dto.VendorDiscountRequest;
import com.payvance.erp_saas.core.dto.VendorDiscountResponse;
import com.payvance.erp_saas.core.entity.VendorDiscount;
import com.payvance.erp_saas.core.repository.VendorDiscountRepository;
import com.payvance.erp_saas.core.repository.VendorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorDiscountService {

    private final VendorDiscountRepository vendorDiscountRepository;
    
    private final VendorRepository vendorRepository;

    /**
     * Upsert a vendor discount
     */
    @Transactional
    public VendorDiscountResponse upsertDiscount(VendorDiscountRequest request) {
        VendorDiscount discount;
        if (request.getId() != null) {
            discount = vendorDiscountRepository.findById(request.getId())
                    .orElse(new VendorDiscount());
        } else {
            discount = new VendorDiscount();
        }

        discount.setType(request.getType());
        discount.setName(request.getName());
        discount.setValue(request.getValue());
        discount.setEffectiveDate(request.getEffectiveDate());

        VendorDiscount saved = vendorDiscountRepository.save(discount);

        return mapToResponse(saved);
    }

    /**
     * Fetch all vendor discounts
     */
    public Page<VendorDiscountResponse> getAllDiscounts(Pageable pageable) {
        Page<VendorDiscount> page = vendorDiscountRepository.findAll(pageable);
        return page.map(this::mapToResponse);
    }


    private VendorDiscountResponse mapToResponse(VendorDiscount discount) {
        VendorDiscountResponse response = new VendorDiscountResponse();
        response.setId(discount.getId());
        response.setType(discount.getType());
        response.setName(discount.getName());
        response.setValue(discount.getValue());
        response.setEffectiveDate(discount.getEffectiveDate());
        return response;
    }
    
	/*
	 * Get the latest vendor discount type and value
	 */
    public Map<String, Object> getDiscount(Long userId) {
        return vendorRepository.findDiscountTypeAndValueByUserId(userId);
    }
    
    public VendorDiscountResponse getDiscountById(Long id) {
        VendorDiscount discount = vendorDiscountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found for id: " + id));
        return mapToResponse(discount);
    }

  

  
}

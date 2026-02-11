package com.payvance.erp_saas.core.controller;

import com.payvance.erp_saas.core.dto.ReferralProgramRequest;
import com.payvance.erp_saas.core.dto.ReferralProgramResponse;
import com.payvance.erp_saas.core.dto.ReferralStatusUpdateRequest;
import com.payvance.erp_saas.core.service.ReferralProgramService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin/referral-programs")
@RequiredArgsConstructor
public class ReferralProgramController {

    private final ReferralProgramService referralProgramService;

    /**
     * Create / Update Referral Program
     */
    @PostMapping
    public ResponseEntity<ReferralProgramResponse> upsertReferralProgram(
            @RequestBody ReferralProgramRequest request) {

        return ResponseEntity.ok(
                referralProgramService.upsertReferralProgram(request)
        );
    }

    /**
     * Get all Referral Programs
     */
    @GetMapping
    public ResponseEntity<Page<ReferralProgramResponse>> getAllReferralPrograms(Pageable pageable) {

        return ResponseEntity.ok(
                referralProgramService.getAllReferralPrograms(pageable)
        );
    }

    
    /**
     * API to update referral status
     */
@PostMapping("/status")
public ResponseEntity<Map<String, Object>> updateReferralStatus(
        @RequestBody ReferralStatusUpdateRequest request) {

    referralProgramService.updateReferralStatus(
            request.getReferralId(),
            request.getStatus()
    );

    return ResponseEntity.ok(Map.of(
            "id", request.getReferralId(),
            "status", request.getStatus(),
            "message", "Referral program status updated"
    ));
}

}

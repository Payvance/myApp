package com.payvance.erp_saas.core.controller;

import com.payvance.erp_saas.core.dto.BankTransferRequestCreateRequest;
import com.payvance.erp_saas.core.dto.BankTransferRequestResponse;
import com.payvance.erp_saas.core.service.BankTransferRequestService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
/**
 * Controller for CA bank transfer settlement APIs
 */
@RestController
@RequestMapping("/api/ca/bank-transfers/settlements")
@RequiredArgsConstructor
public class BankTransferRequestController {

    private final BankTransferRequestService service;

    /**
     * Fetch bank transfer settlements for logged-in tenant (CA)
     * userId is taken from header and mapped with bank_transfer_requests.tenant_id
     */
    @GetMapping
    public ResponseEntity<Page<BankTransferRequestResponse>> getBankTransfers(
            @RequestHeader("userId") Long userId,   // tenantId
            @RequestParam(required = false) String status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                service.getBankTransfers(userId, status, pageable)
        );
    }

    
    /**
     * Create bank transfer request
     * tenant_id is taken from header (X-USER-ID)
     */
   @PostMapping
public ResponseEntity<Map<String, String>> createBankTransferRequest(
        @RequestHeader("userId") Long userId,   // tenantId
        @RequestBody BankTransferRequestCreateRequest request
) {
    service.createBankTransferRequest(userId, request);

    return ResponseEntity.ok(
            Map.of(
                "message", "Request raised successfully",
                "status", "SUCCESS"
            )
    );
}

}

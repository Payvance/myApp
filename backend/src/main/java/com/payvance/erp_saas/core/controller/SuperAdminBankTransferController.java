package com.payvance.erp_saas.core.controller;

import com.payvance.erp_saas.core.dto.BankTransferRequestResponse;
import com.payvance.erp_saas.core.dto.SuperAdminSettlementDetailResponse;
import com.payvance.erp_saas.core.service.BankTransferRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.payvance.erp_saas.core.dto.BankTransferApprovalRequest;
@RestController
@RequestMapping("/api/super-admin/bank-transfers/settlements")
@RequiredArgsConstructor
public class SuperAdminBankTransferController {

    private final BankTransferRequestService service;

    /**
     * Fetch all bank transfer settlements for Super Admin
     */
    @GetMapping
    public ResponseEntity<Page<BankTransferRequestResponse>> getAllSettlements(
            @RequestParam(required = false) String status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                service.getAllBankTransfers(status, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuperAdminSettlementDetailResponse> getSettlementDetail(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.getSettlementDetail(id)
        );
    }
    
     /**
     * Approve / Reject settlement (updates bank_transfer_requests + referrals)
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approveSettlement(
            @PathVariable Long id,
            @RequestBody BankTransferApprovalRequest request
    ) {
        service.approveSettlement(id, request);
        return ResponseEntity.ok().build();
    }

}

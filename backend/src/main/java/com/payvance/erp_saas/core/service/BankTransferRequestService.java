package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.dto.BankTransferApprovalRequest;
import com.payvance.erp_saas.core.dto.BankTransferRequestCreateRequest;
import com.payvance.erp_saas.core.dto.BankTransferRequestResponse;
import com.payvance.erp_saas.core.dto.SuperAdminSettlementDetailResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for bank transfer requests
 */
public interface BankTransferRequestService {

    Page<BankTransferRequestResponse> getBankTransfers(
            Long tenantId,     // mapped from header userId
            String status,
            Pageable pageable
    );


    /**
        * Service interface for bank transfer requests
    */
    void createBankTransferRequest(
            Long tenantId,
            BankTransferRequestCreateRequest request
    );

    /**
     * Fetch all bank transfer settlements for Super Admin  
     */
    Page<BankTransferRequestResponse> getAllBankTransfers(
        String status,
        Pageable pageable
   );

   SuperAdminSettlementDetailResponse getSettlementDetail(
        Long bankTransferId
    );


    void approveSettlement(
            Long bankTransferId,
            BankTransferApprovalRequest request
    );


}

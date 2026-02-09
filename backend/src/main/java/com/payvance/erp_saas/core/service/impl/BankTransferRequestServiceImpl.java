package com.payvance.erp_saas.core.service.impl;

import com.payvance.erp_saas.core.dto.BankTransferApprovalRequest;
import com.payvance.erp_saas.core.dto.BankTransferRequestCreateRequest;
import com.payvance.erp_saas.core.dto.BankTransferRequestResponse;
import com.payvance.erp_saas.core.dto.SettlementReferralResponse;
import com.payvance.erp_saas.core.dto.SuperAdminSettlementDetailResponse;
import com.payvance.erp_saas.core.entity.BankTransferRequest;
import com.payvance.erp_saas.core.repository.BankTransferRequestRepository;
import com.payvance.erp_saas.core.repository.ReferralRepository;
import com.payvance.erp_saas.core.service.BankTransferRequestService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of bank transfer request service
 */
@Service
@RequiredArgsConstructor
public class BankTransferRequestServiceImpl implements BankTransferRequestService {

    private final BankTransferRequestRepository repository;
    private final BankTransferRequestRepository bankTransferRepository;
    private final ReferralRepository referralRepository;

    @Override
    public Page<BankTransferRequestResponse> getBankTransfers(
            Long tenantId,
            String status,
            Pageable pageable
    ) {
        return repository.fetchBankTransfers(
                tenantId,
                status,
                pageable
        );
    }

    /**
     * Creates a bank transfer request and links it to referrals
     */
    @Override
    @Transactional
    public void createBankTransferRequest(
            Long tenantId,
            BankTransferRequestCreateRequest request
    ) {
        // 1️⃣ Create bank transfer request
        BankTransferRequest bankTransfer = new BankTransferRequest();
        bankTransfer.setTenantId(tenantId);
        bankTransfer.setReferralsCount(request.getReferralsCount()); // number of referrals included
        bankTransfer.setAmount(request.getAmount());
        bankTransfer.setStatus("PENDING");

        BankTransferRequest savedTransfer =
                bankTransferRepository.save(bankTransfer);

        // 2️⃣ Update referrals with generated bank_transfer_id
        referralRepository.updateBankTransferId(
                savedTransfer.getId(),
                "REQUEST_RAISED",  
                request.getReferralIds()
        );
    }

        /**
         * Fetch all bank transfer settlements for Super Admin  
         */
    @Override
        public Page<BankTransferRequestResponse> getAllBankTransfers(
                String status,
                Pageable pageable
        ) {
        return repository.fetchAllBankTransfers(status, pageable);
        }


        @Override
@Transactional(readOnly = true)
public SuperAdminSettlementDetailResponse getSettlementDetail(
        Long bankTransferId
) {
    // Fetch bank transfer request
    BankTransferRequest bankTransfer =
            repository.findById(bankTransferId)
                    .orElseThrow(() ->
                            new RuntimeException("Bank transfer request not found")
                    );

    //  Fetch linked referrals
    List<SettlementReferralResponse> referrals =
            referralRepository.findAllByBankTransferId(bankTransferId);

    // Build response
    return new SuperAdminSettlementDetailResponse(
            bankTransfer.getId(),
            bankTransfer.getTenantId(),
            bankTransfer.getReferralsCount(),
            bankTransfer.getAmount(),
            bankTransfer.getStatus(),
            bankTransfer.getCreatedAt(),
            referrals
    );
}

@Override
@Transactional
public void approveSettlement(
        Long bankTransferId,
        BankTransferApprovalRequest request
) {
    // 1️⃣ Update bank_transfer_requests
    bankTransferRepository.updateSettlementApproval(
            bankTransferId,
            request.getStatus(),
            request.getPaymentMode(),
            request.getUtrNumber(),
            request.getPayerBank(),
            request.getPaidAmount(),
            request.getPaidDate()
    );

    // 2️⃣ Update referrals status
    referralRepository.updateStatusByBankTransferId(
            bankTransferId,
            request.getStatus()
    );
}


}

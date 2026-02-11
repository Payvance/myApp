package com.payvance.erp_saas.core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payvance.erp_saas.core.dto.ReferralDetailsResponse;
import com.payvance.erp_saas.core.dto.ReferralProgramRequest;
import com.payvance.erp_saas.core.dto.ReferralProgramResponse;
import com.payvance.erp_saas.core.entity.ReferralCode;
import com.payvance.erp_saas.core.entity.ReferralProgram;
import com.payvance.erp_saas.core.repository.ReferralCodeRepository;
import com.payvance.erp_saas.core.repository.ReferralProgramRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReferralProgramService {

    private final ReferralProgramRepository referralProgramRepository;
    private final ReferralCodeRepository referralCodeRepository;

    /**
     * Create or Update Referral Program
     */
    @Transactional
    public ReferralProgramResponse upsertReferralProgram(ReferralProgramRequest request) {

        ReferralProgram referralProgram = request.getId() != null
                ? referralProgramRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("Referral Program not found"))
                : new ReferralProgram();

        referralProgram.setCode(request.getCode());
        referralProgram.setOwnerType(request.getOwnerType());
        referralProgram.setName(request.getName());
        referralProgram.setRewardType(request.getRewardType());
        referralProgram.setRewardValue(request.getRewardValue());
        referralProgram.setRewardPercentage(request.getRewardPercentage());
        referralProgram.setMaxPerReferrer(request.getMaxPerReferrer());
        referralProgram.setStatus(request.getStatus());

        ReferralProgram saved = referralProgramRepository.save(referralProgram);

        return mapToResponse(saved);
    }

    /**
     * Get all Referral Programs
     */
    public Page<ReferralProgramResponse> getAllReferralPrograms(Pageable pageable) {
        return referralProgramRepository.findAll(pageable)
                .map(this::mapToResponse);

    }

    private ReferralProgramResponse mapToResponse(ReferralProgram entity) {
        return ReferralProgramResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .ownerType(entity.getOwnerType())
                .name(entity.getName())
                .rewardType(entity.getRewardType())
                .rewardValue(entity.getRewardValue())
                .rewardPercentage(entity.getRewardPercentage())
                .maxPerReferrer(entity.getMaxPerReferrer())
                .status(entity.getStatus())
                .build();
    }

    /**
 * Update the status of a referral program
 *
 * @param referralId Referral Program ID
 * @param status     New status sent from frontend
 */
@Transactional
public void updateReferralStatus(Long referralId, String status) {

    ReferralProgram referralProgram = referralProgramRepository.findById(referralId)
            .orElseThrow(() -> new RuntimeException("Referral Program not found"));

    referralProgram.setStatus(status);
    referralProgramRepository.save(referralProgram);
}

public ReferralDetailsResponse getReferralDetails(Long tenantId) {

    // Get referral code by tenant
    ReferralCode referralCode = referralCodeRepository
            .findByOwnerIdAndStatus(tenantId, "active")
            .orElseThrow(() ->
                    new RuntimeException("Active referral code not found"));

    // Get program using programId (NO mapping)
    ReferralProgram program = referralProgramRepository
            .findById(referralCode.getProgramId())
            .orElseThrow(() ->
                    new RuntimeException("Referral program not found"));

    //  Build response
    return ReferralDetailsResponse.builder()
            .referralCode(referralCode.getCode())
            .programId(program.getId())
            .programCode(program.getCode())
            .programName(program.getName())
            .rewardType(program.getRewardType())
            .rewardValue(program.getRewardValue())
            .rewardPercentage(program.getRewardPercentage())
            .programStatus(program.getStatus())
            .build();
}
}

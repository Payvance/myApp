/**
 * Copyright: Â© 2024 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of Payvance Innovation Pvt. Ltd.
 * Payvance Innovation Pvt. Ltd. retains all title to and intellectual property rights in these materials.
 *
 **/

/**
 *
 * @author           version     date        change description
 * Aniiket.Desai 	 1.0.0       07-Jan-2026    class created
 *
 **/
package com.payvance.erp_saas.core.service;

import java.util.Comparator;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.payvance.erp_saas.core.dto.RejectedUserDto;
import com.payvance.erp_saas.core.dto.TenantUserResponseDto;
import com.payvance.erp_saas.core.dto.UserFullDetailsDto;
import com.payvance.erp_saas.core.dto.UserResponseDTO;
import com.payvance.erp_saas.core.dto.UserUpdateRequestDto;
import com.payvance.erp_saas.core.entity.Ca;
import com.payvance.erp_saas.core.entity.ReferralCode;
import com.payvance.erp_saas.core.entity.ReferralProgram;
import com.payvance.erp_saas.core.entity.Role;
import com.payvance.erp_saas.core.entity.User;
import com.payvance.erp_saas.core.entity.Vendor;
import com.payvance.erp_saas.core.repository.BankDetailsRepository;
import com.payvance.erp_saas.core.repository.CaRepository;
import com.payvance.erp_saas.core.repository.ReferralCodeRepository;
import com.payvance.erp_saas.core.repository.ReferralProgramRepository;
import com.payvance.erp_saas.core.repository.RoleRepository;
import com.payvance.erp_saas.core.repository.UserAddressRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
import com.payvance.erp_saas.core.repository.VendorRepository;
import com.payvance.erp_saas.core.util.ReferralCodeUtil;
import com.payvance.erp_saas.exceptions.UserNotAllowedException;
import com.payvance.erp_saas.exceptions.UserNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    
    private final CaRepository caRepository;
    
    private final VendorRepository vendorRepository;
    
    private final UserAddressRepository userAddressRepository;
    
    private final BankDetailsRepository bankDetailsRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final EmailService emailService;
    
    private final ReferralProgramRepository referralProgramRepository;
    
    private final ReferralCodeRepository referralCodeRepository;
    private final RoleRepository roleRepository;
    
    private final WalletService walletService;

    public UserResponseDTO getUserDetails(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        UserResponseDTO response = new UserResponseDTO();
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());

        return response;
    }
    
    /*
     * Get paginated list of users with basic details
     */
    public Page<Map<String, Object>> getUsers(Pageable pageable) {
        return userRepository.findAllUsersBasic(pageable);
    }
    
    /*
     * Get paginated list of inactive users
     */
    public Page<Map<String, Object>> getInactiveUsers(Pageable pageable) {
        return userRepository.findPendingVendorAndCaUsers(pageable);
    }
     
    /*
     * Get paginated list of inactive users
     */
//    public Page<Map<String, Object>> getRejectedUsers(Pageable pageable) {
//        return userRepository.findRejectedUsers(pageable);
//    
    public Page<RejectedUserDto> getRejectedUsers(Pageable pageable) {
        return userRepository.findRejectedUsers(pageable);
    }
    
    /*
     * Get full details of a user by userId
     */
    public UserFullDetailsDto getUserDetail(Long userId) {
        return userRepository.findFullDetailsByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
    }
    
    
    /*
     * Update user details including related entities
     * @param userId the ID of the user to update
     * @param dto the DTO containing updated user details
     * @return void
     */
    @Transactional
    public void updateUserDetails(Long userId, UserUpdateRequestDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        /* ================= USER ================= */
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getIsActive() != null) user.setActive(dto.getIsActive());

        userRepository.save(user);

        /* ================= CA ================= */
        caRepository.findByUserId(userId).ifPresent(ca -> {

            if (dto.getCaRegNo() != null) ca.setCaRegNo(dto.getCaRegNo());
            if (dto.getEnrollmentYear() != null) ca.setEnrollmentYear(dto.getEnrollmentYear());
            if (dto.getIcaiMemberStatus() != null) ca.setIcaiMemberStatus(dto.getIcaiMemberStatus());
            if (dto.getPracticeType() != null) ca.setPracticeType(dto.getPracticeType());
            if (dto.getFirmName() != null) ca.setFirmName(dto.getFirmName());
            if (dto.getIcaiMemberNo() != null) ca.setIcaiMemberNo(dto.getIcaiMemberNo());
            if (dto.getCaStatus() != null) ca.setStatus(dto.getCaStatus());

            caRepository.save(ca);
        });

        /* ================= VENDOR ================= */
        vendorRepository.findByUserId(userId).ifPresent(vendor -> {

            if (dto.getVendorName() != null) vendor.setName(dto.getVendorName());
            if (dto.getVendorType() != null) vendor.setVendorType(dto.getVendorType());
            if (dto.getExperienceYears() != null) vendor.setExperienceYears(dto.getExperienceYears());
            if (dto.getGstNo() != null) vendor.setGstNo(dto.getGstNo());
            if (dto.getCinNo() != null) vendor.setCinNo(dto.getCinNo());
            if (dto.getPanNo() != null) vendor.setPanNo(dto.getPanNo());
            if (dto.getTanNo() != null) vendor.setTanNo(dto.getTanNo());
            if (dto.getAadharNo() != null) vendor.setAadharNo(dto.getAadharNo());
            if (dto.getVendorStatus() != null) vendor.setStatus(dto.getVendorStatus());
            if(dto.getVendorDiscountId() != null) vendor.setVendorDiscountId(dto.getVendorDiscountId());

            vendorRepository.save(vendor);
        });

        /* ================= ADDRESS ================= */
        userAddressRepository.findByUserId(userId).ifPresent(addr -> {

            if (dto.getHouseBuildingNo() != null) addr.setHouseBuildingNo(dto.getHouseBuildingNo());
            if (dto.getHouseBuildingName() != null) addr.setHouseBuildingName(dto.getHouseBuildingName());
            if (dto.getRoadAreaPlace() != null) addr.setRoadAreaPlace(dto.getRoadAreaPlace());
            if (dto.getLandmark() != null) addr.setLandmark(dto.getLandmark());
            if (dto.getVillage() != null) addr.setVillage(dto.getVillage());
            if (dto.getTaluka() != null) addr.setTaluka(dto.getTaluka());
            if (dto.getCity() != null) addr.setCity(dto.getCity());
            if (dto.getDistrict() != null) addr.setDistrict(dto.getDistrict());
            if (dto.getState() != null) addr.setState(dto.getState());
            if (dto.getPincode() != null) addr.setPincode(dto.getPincode());
            if (dto.getPostOffice() != null) addr.setPostOffice(dto.getPostOffice());
            if (dto.getCountry() != null) addr.setCountry(dto.getCountry());

            userAddressRepository.save(addr);
        });

        /* ================= BANK ================= */
        bankDetailsRepository.findByUserId(userId).ifPresent(bank -> {

            if (dto.getBankName() != null) bank.setBankName(dto.getBankName());
            if (dto.getBranchName() != null) bank.setBranchName(dto.getBranchName());
            if (dto.getAccountNumber() != null) bank.setAccountNumber(dto.getAccountNumber());
            if (dto.getIfscCode() != null) bank.setIfscCode(dto.getIfscCode());

            bankDetailsRepository.save(bank);
        });
    }
    
    
  /*
   *   * Update user profile status - approve or reject with remark
   *   @param id the ID of the user
   *   @param approve true to approve, false to reject
   *   @param remark optional remark for rejection
   *   @return updated User entity
   */
    @Transactional
    public User updateProfileStatus(Long id, Boolean approve, String remark) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ---- find pending role ----
        Vendor vendor = vendorRepository
                .findByUserIdAndStatus(id, "PENDING_APPROVAL")
                .orElse(null);

        Ca ca = caRepository
                .findByUserIdAndStatus(id, "PENDING_APPROVAL")
                .orElse(null);

        if (vendor == null && ca == null) {
            throw new RuntimeException("No pending approval found");
        }

        if (Boolean.FALSE.equals(approve)
                && (remark == null || remark.isBlank())) {
            throw new IllegalArgumentException("Remark is required for rejection");
        }

        // ================= APPROVE =================
        if (Boolean.TRUE.equals(approve)) {

            if (vendor != null) {
                vendor.setStatus("approved");
                vendor.setRejectionRemark(null);
                vendorRepository.save(vendor);
                
                walletService.createWalletIfNotExists(
                        vendor,
                        vendor.getId()
                );
            }

            if (ca != null) {
                ca.setStatus("approved");
                ca.setRejectionRemark(null);
                caRepository.save(ca);
                
                walletService.createWalletIfNotExists(
                        ca,
                        ca.getId()
                );
            
             // Fetch ACTIVE referral program for CA role 
                ReferralProgram caProgram = referralProgramRepository
                        .findTopByStatusAndRoleIdOrderByCreatedAtDesc("ACTIVE", 5L)
                        .orElseThrow(() -> new RuntimeException("No active referral program found for CA"));

                // Resolve role dynamically
                Role caRole = roleRepository.findById(caProgram.getRoleId())
                        .orElseThrow(() -> new RuntimeException("Role not found for id: " + caProgram.getRoleId()));

                String caRoleCode = caRole.getCode();

                // Check if referral code exists
                boolean caExists = referralCodeRepository.existsByProgramIdAndOwnerId(caProgram.getId(), ca.getId());
                if (!caExists) {
                    ReferralCode caReferral = new ReferralCode();
                    caReferral.setProgramId(caProgram.getId());
                    caReferral.setOwnerId(user.getId());
                    caReferral.setOwnerType(caRoleCode);
                    caReferral.setCode(ReferralCodeUtil.buildCaReferralCode(ca.getId(), user.getName()));
                    caReferral.setStatus("ACTIVE");
                    caReferral.setMaxUses(0);
                    caReferral.setUsedCount(0);

                    referralCodeRepository.save(caReferral);
                    }
                }

            emailService.sendProfileApprovalRejectionEmail(
                    user.getId(),
                    true,
                    null,
                    null
            );
        }

        // ================= REJECT =================
        else {

            if (vendor != null) {
                vendor.setStatus("rejected");
                vendor.setRejectionRemark(remark);
                vendorRepository.save(vendor);
            }

            if (ca != null) {
                ca.setStatus("rejected");
                ca.setRejectionRemark(remark);
                caRepository.save(ca);
            }

            String referralCode = null;
			emailService.sendProfileApprovalRejectionEmail(
                    user.getId(),
                    false,
                    remark,
                    referralCode
            );
        }

        return userRepository.save(user);
    }

    /*
     * Reset user password by phone number
     * @param phone the user's phone number
     * @param newPassword the new password to set
     * @return void
     */
    public void resetPasswordByEmail(String email, String newPassword) {

    	  User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotAllowedException("User not found"));

        if (!user.isActive()) {
            throw new UserNotAllowedException("User is inactive");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
	public boolean isPhoneExist(String Phone) {
		return userRepository.findByPhone(Phone).isPresent();
	}
	
	 public Page<TenantUserResponseDto> getTenantUsers(Long tenantId, Pageable pageable) {
	        return userRepository.findTenantUsersByTenantId(tenantId, pageable);
	    }
	 
	 public TenantUserResponseDto getTenantUserById(Long tenantId, Long userId) {
		    return userRepository
		            .findTenantUserByTenantIdAndUserId(tenantId, userId)
		            .orElseThrow(() -> new RuntimeException("Tenant user not found"));
		}
}

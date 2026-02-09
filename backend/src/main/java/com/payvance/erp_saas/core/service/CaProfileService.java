package com.payvance.erp_saas.core.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payvance.erp_saas.core.dto.CaRequest;
import com.payvance.erp_saas.core.dto.ProfileResponse;
import com.payvance.erp_saas.core.entity.BankDetails;
import com.payvance.erp_saas.core.entity.Ca;
import com.payvance.erp_saas.core.entity.UserAddress;
import com.payvance.erp_saas.core.repository.CaRepository;
import com.payvance.erp_saas.core.repository.UserRepository;

import lombok.RequiredArgsConstructor;
// Service for managing CA profiles including upserting profile, address, and bank details
@Service
@RequiredArgsConstructor
public class CaProfileService {

    private final CaRepository caRepository;
    private final AddressService addressService;
    private final BankService bankService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    
    @Transactional
    public ProfileResponse upsertCaProfile(CaRequest request) {

        Long userId = request.getUserId();

        
        // 2. Fetch the registered email from the User table
        String registeredEmail = userRepository.findById(userId)
                .map(user -> user.getEmail())
                .orElse(null);
        
        Ca ca;

        // Check if CA exists
        Optional<Ca> existingCa = caRepository.findByUserId(userId);
        if (existingCa.isPresent()) {

            ca = existingCa.get();

            // ===== BASIC INFO =====
            ca.setName(request.getName());
            ca.setEmail(request.getEmail());
            ca.setPhone(request.getPhone());

            // ===== CA SPECIFIC =====
            ca.setCaRegNo(request.getCaRegNo());
            ca.setEnrollmentYear(request.getEnrollmentYear());
            ca.setIcaiMemberStatus(request.getIcaiMemberStatus());
            ca.setPracticeType(request.getPracticeType());
            ca.setFirmName(request.getFirmName());
            ca.setIcaiMemberNo(request.getIcaiMemberNo());
            ca.setAadharNo(request.getAadharNo());
            ca.setGstNo(request.getGstNo());
            ca.setCinNo(request.getCinNo());
            ca.setPanNo(request.getPanNo());
            ca.setTanNo(request.getTanNo());
            ca.setCaType(request.getCaType());
            if (request.getStatus() != null) {
                ca.setStatus(request.getStatus());
            }

        } else {

            ca = new Ca();
            ca.setUserId(userId);

            // ===== BASIC INFO =====
            ca.setName(request.getName());
            ca.setEmail(request.getEmail());
            ca.setPhone(request.getPhone());

            // ===== CA SPECIFIC =====
            ca.setCaRegNo(request.getCaRegNo());
            ca.setEnrollmentYear(request.getEnrollmentYear());
            ca.setIcaiMemberStatus(request.getIcaiMemberStatus());
            ca.setPracticeType(request.getPracticeType());
            ca.setFirmName(request.getFirmName());
            ca.setIcaiMemberNo(request.getIcaiMemberNo());
            ca.setAadharNo(request.getAadharNo());
            
            ca.setGstNo(request.getGstNo());
            ca.setCinNo(request.getCinNo());
            ca.setPanNo(request.getPanNo());
            ca.setTanNo(request.getTanNo());
            ca.setCaType(request.getCaType());

            // ===== STATUS =====
            ca.setStatus(request.getStatus() != null ? request.getStatus() : "active");
        }


        // Upsert Address
        UserAddress address = new UserAddress();
        address.setHouseBuildingNo(request.getHouseBuildingNo());
        address.setHouseBuildingName(request.getHouseBuildingName());
        address.setRoadAreaPlace(request.getRoadAreaPlace());
        address.setLandmark(request.getLandmark());
        address.setVillage(request.getVillage());
        address.setTaluka(request.getTaluka());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setPostOffice(request.getPostOffice());
        address.setCountry(request.getCountry());

        UserAddress savedAddress = addressService.upsertAddress(userId, address);
        ca.setAddressId(savedAddress.getId());

        // Upsert Bank
        BankDetails bank = new BankDetails();
        bank.setBankName(request.getBankName());
        bank.setBranchName(request.getBranchName());
        bank.setAccountNumber(request.getAccountNumber());
        bank.setIfscCode(request.getIfscCode());

        BankDetails savedBank = bankService.upsertBank(userId, bank);
        ca.setBankDetailsId(savedBank.getId());

        ca = caRepository.save(ca);
        
        if (registeredEmail != null && !registeredEmail.isBlank()) {
            emailService.sendProfileSubmittedEmail(userId);
        }

        return ProfileResponse.builder()
                .id(ca.getId())
                .name(ca.getName())
                .email(ca.getEmail())
                .phone(ca.getPhone())
                .status(ca.getStatus())
                .build();
    }
}

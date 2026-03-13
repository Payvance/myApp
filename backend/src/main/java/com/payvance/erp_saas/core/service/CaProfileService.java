package com.payvance.erp_saas.core.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payvance.erp_saas.core.dto.CaRequest;
import com.payvance.erp_saas.core.dto.LoginResponse;
import com.payvance.erp_saas.core.dto.ProfileResponse;
import com.payvance.erp_saas.core.entity.BankDetails;
import com.payvance.erp_saas.core.entity.Ca;
import com.payvance.erp_saas.core.entity.PersonalAccessToken;
import com.payvance.erp_saas.core.entity.Role;
import com.payvance.erp_saas.core.entity.UserAddress;
import com.payvance.erp_saas.core.repository.CaRepository;
import com.payvance.erp_saas.core.repository.DocumentsRepository;
import com.payvance.erp_saas.core.repository.PersonalAccessTokenRepository;
import com.payvance.erp_saas.core.repository.RoleRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
import com.payvance.erp_saas.core.entity.Documents;
import com.payvance.erp_saas.security.util.JwtUtil;

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
    private final JwtUtil jwt;
    private final PersonalAccessTokenRepository patRepo;
    private final RoleRepository roleRepository;
    private final DocumentsRepository documentsRepository;

    @Transactional
    public LoginResponse upsertCaProfile(CaRequest request, String token) {

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

        // ===== UPSERT DOCUMENTS =====
        Documents documents = documentsRepository.findByCaId(ca.getId()).orElse(new Documents());
        documents.setCa(ca);
        if (request.getPanDocument() != null)
            documents.setPanDocument(request.getPanDocument());
        if (request.getMsmeDocument() != null)
            documents.setMsmeDocument(request.getMsmeDocument());
        if (request.getGstDocument() != null)
            documents.setGstCertificate(request.getGstDocument());
        documentsRepository.save(documents);

        String tokenId = jwt.getTokenId(token);
        patRepo.deleteByTokenId(tokenId);

        // ===== GENERATE NEW CA TOKEN =====
        Role role = roleRepository.findById(5L)
                .orElseThrow(() -> new RuntimeException("CA role not found"));

        Long roleId = role.getId();

        String newToken = jwt.generateAccessToken(userId, null, roleId);
        // Save new PAT
        patRepo.save(PersonalAccessToken.builder()
                .tokenId(jwt.getTokenId(newToken))
                .userId(userId)
                .tenantId(null)
                .tokenableId(userId)
                .tokenableType(role.getCode())
                .name("ACCESS_TOKEN")
                .expiresAt(jwt.getExpiration(newToken))
                .build());

        if (registeredEmail != null && !registeredEmail.isBlank()) {
            emailService.sendProfileSubmittedEmail(userId);
        }

        return LoginResponse.builder()
                .accessToken(newToken)
                .roleId(roleId)
                .userId(userId)
                .build();
    }
}

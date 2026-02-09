package com.payvance.erp_saas.core.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payvance.erp_saas.core.dto.ProfileResponse;
import com.payvance.erp_saas.core.dto.VendorRequest;
import com.payvance.erp_saas.core.entity.BankDetails;
import com.payvance.erp_saas.core.entity.UserAddress;
import com.payvance.erp_saas.core.entity.Vendor;
import com.payvance.erp_saas.core.repository.UserRepository;
import com.payvance.erp_saas.core.repository.VendorRepository;

import lombok.RequiredArgsConstructor;
// Service for managing vendor profiles including upserting profile, address, and bank details
@Service
@RequiredArgsConstructor
public class VendorProfileService {

    private final VendorRepository vendorRepository;
    private final AddressService addressService;
    private final BankService bankService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Transactional
    public ProfileResponse upsertVendorProfile(VendorRequest request) {

        Long userId = request.getUserId();
        String registeredEmail = userRepository.findById(userId)
                .map(user -> user.getEmail())
                .orElse(null);
        
        Vendor vendor;

        // Check if vendor exists
        Optional<Vendor> existingVendor = vendorRepository.findByUserId(userId);
        if (existingVendor.isPresent()) {

            vendor = existingVendor.get();

            // ===== BASIC INFO =====
            vendor.setName(request.getName());
            vendor.setEmail(request.getEmail());
            vendor.setPhone(request.getPhone());

            // ===== VENDOR SPECIFIC =====
            vendor.setVendorType(request.getVendorType());
            vendor.setExperienceYears(request.getExperienceYears());
            vendor.setVendorDiscountId(request.getVendorDiscountId());

            // ===== LEGAL DETAILS =====
            vendor.setGstNo(request.getGstNo());
            vendor.setCinNo(request.getCinNo());
            vendor.setPanNo(request.getPanNo());
            vendor.setTanNo(request.getTanNo());
            vendor.setAadharNo(request.getAadharNo());

            if (request.getStatus() != null) {
                vendor.setStatus(request.getStatus());
            }

        } else {

            vendor = new Vendor();
            vendor.setUserId(userId);

            // ===== BASIC INFO =====
            vendor.setName(request.getName());
            vendor.setEmail(request.getEmail());
            vendor.setPhone(request.getPhone());

            // ===== VENDOR SPECIFIC =====
            vendor.setVendorType(request.getVendorType());
            vendor.setExperienceYears(request.getExperienceYears());

            // ===== LEGAL DETAILS =====
            vendor.setGstNo(request.getGstNo());
            vendor.setCinNo(request.getCinNo());
            vendor.setPanNo(request.getPanNo());
            vendor.setTanNo(request.getTanNo());
            vendor.setAadharNo(request.getAadharNo());

            // ===== STATUS =====
            vendor.setStatus(request.getStatus() != null ? request.getStatus() : "active");
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
        vendor.setAddressId(savedAddress.getId());

        // Upsert Bank
        BankDetails bank = new BankDetails();
        bank.setBankName(request.getBankName());
        bank.setBranchName(request.getBranchName());
        bank.setAccountNumber(request.getAccountNumber());
        bank.setIfscCode(request.getIfscCode());

        BankDetails savedBank = bankService.upsertBank(userId, bank);
        vendor.setBankDetailsId(savedBank.getId());

        vendor = vendorRepository.save(vendor);
        
        if (registeredEmail != null && !registeredEmail.isBlank()) {
            emailService.sendProfileSubmittedEmail(userId);
        }

        return ProfileResponse.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .email(vendor.getEmail())
                .phone(vendor.getPhone())
                .status(vendor.getStatus())
                .build();
    }
}

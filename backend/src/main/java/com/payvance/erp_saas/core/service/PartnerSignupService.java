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
 * om            	 1.0.0       05-Jan-2026    class created
 *
 **/
package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.dto.TenantSignupRequest;
import com.payvance.erp_saas.core.entity.Ca;
import com.payvance.erp_saas.core.entity.User;
import com.payvance.erp_saas.core.entity.Vendor;
import com.payvance.erp_saas.core.entity.Wallet;
import com.payvance.erp_saas.core.enums.RoleEnum;
import com.payvance.erp_saas.core.repository.CaRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
import com.payvance.erp_saas.core.repository.VendorRepository;
import com.payvance.erp_saas.core.repository.WalletRepository;
import com.payvance.erp_saas.exceptions.DuplicateEntryException;
import com.payvance.erp_saas.exceptions.UserNotAllowedException;
import com.payvance.erp_saas.security.util.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartnerSignupService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final CaRepository caRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Transactional
    public void signupPartner(TenantSignupRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateEntryException("Email already registered");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setPhone(req.getPhone());
        user.setActive(true);
        user.setSuperadmin(false);
        userRepository.save(user);
        RoleEnum role = RoleEnum.fromId(req.getRole());
        if (RoleEnum.VENDOR==role) {

            Vendor vendor = new Vendor();
            vendor.setUserId(user.getId());
            vendor.setStatus("draft");
            // Additional fields specific to CA entity
            vendor.setName(req.getName());
            vendor.setEmail(req.getEmail());
            vendor.setPhone(req.getPhone());
            vendorRepository.save(vendor);

            createWallet("vendor", vendor.getId());

        } else if (RoleEnum.CA==role) {

            Ca ca = new Ca();
            ca.setUserId(user.getId());
            ca.setStatus("draft");
            // Additional fields specific to CA entity
            ca.setName(req.getName());
            ca.setEmail(req.getEmail());
            ca.setPhone(req.getPhone());
            caRepository.save(ca);

            createWallet("ca", ca.getId());

        } else {
            throw new UserNotAllowedException("Invalid partner role");
        }

        String token = jwtUtil.generateEmailVerificationToken(user.getId(), user.getEmail());
        emailService.sendVerificationEmail(user.getEmail(), token);
    }

    private void createWallet(String ownerType, Long ownerId) {
        if (!walletRepository.existsByOwnerTypeAndOwnerId(ownerType, ownerId)) {
            Wallet wallet = new Wallet();
            wallet.setOwnerType(ownerType);
            wallet.setOwnerId(ownerId);
            walletRepository.save(wallet);
        }
    }
}

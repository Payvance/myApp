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
 * Aniket Desai  	 1.0.0       06-Jan-2026    class created
 *
 **/
package com.payvance.erp_saas.core.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.payvance.erp_saas.core.dto.ActivationKeySearchRequestDto;
import com.payvance.erp_saas.core.dto.ApprovedBatchDropdownDto;
import com.payvance.erp_saas.core.dto.IssueLicenseRequestDto;
import com.payvance.erp_saas.core.dto.VendorActivationBatchResponseDTO;
import com.payvance.erp_saas.core.dto.VendorBatchRequestDto;
import com.payvance.erp_saas.core.dto.VendorBatchResponseDto;
import com.payvance.erp_saas.core.dto.VendorBatchSearchRequestDto;
import com.payvance.erp_saas.core.entity.ActivationKey;
import com.payvance.erp_saas.core.entity.Plan;
import com.payvance.erp_saas.core.entity.User;
import com.payvance.erp_saas.core.entity.VendorActivationBatch;
import com.payvance.erp_saas.core.entity.VendorPaymentUpload;
import com.payvance.erp_saas.core.entity.VendorTenants;
import com.payvance.erp_saas.core.repository.ActivationKeyRepository;
import com.payvance.erp_saas.core.repository.PlanRepository;
import com.payvance.erp_saas.core.repository.UserRepository;
import com.payvance.erp_saas.core.repository.VendorActivationBatchRepository;
import com.payvance.erp_saas.core.repository.VendorPaymentUploadRepository;
import com.payvance.erp_saas.core.repository.VendorRepository;
import com.payvance.erp_saas.core.repository.VendorTenantsRepository;
import com.payvance.erp_saas.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorLicenseService {

    private final VendorActivationBatchRepository batchRepository;
    private final ActivationKeyRepository activationKeyRepository;
    private final VendorPaymentUploadRepository paymentRepository;
    private final PlanRepository planRepository;
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final VendorTenantsRepository vendorTenantsRepository;

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final Random random = new SecureRandom();
    
    private final EmailService emailService;

    @Transactional
    public VendorActivationBatch createBatch(VendorBatchRequestDto request) {

        // Fetch Plan entity
        Plan plan = planRepository.findById(request.getLicenseModelId())
                .orElseThrow(() -> new RuntimeException("Plan not found with ID: " + request.getLicenseModelId()));

        VendorActivationBatch batch = new VendorActivationBatch();
        batch.setPlan(plan);
        batch.setVendorId(request.getVendorId());
        batch.setLicenseModelId(request.getLicenseModelId());
        batch.setVendorDiscountId(request.getVendorDiscountId());
        batch.setTotalActivations(request.getTotalActivations());
        batch.setUsedActivations(0);
        batch.setCostPrice(request.getCostPrice());
        batch.setResalePrice(request.getResalePrice());
        batch.setCurrency(request.getCurrency());

        if (hasPaymentDetails(request)) {
            batch.setStatus("Pending");
        } else {
            batch.setStatus("Pending Payment");
        }

        VendorActivationBatch savedBatch = batchRepository.save(batch);

        if (hasPaymentDetails(request)) {
            savePaymentDetails(savedBatch.getId(), request);
        }

        return savedBatch;
    }

    @Transactional
    public VendorActivationBatch updateBatch(Long id, VendorBatchRequestDto request) {
        VendorActivationBatch batch = batchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Batch not found with id: " + id));

        if (!"Approved".equalsIgnoreCase(batch.getStatus())) {
            if (request.getTotalActivations() != null)
                batch.setTotalActivations(request.getTotalActivations());
            if (request.getCostPrice() != null)
                batch.setCostPrice(request.getCostPrice());
            if (request.getResalePrice() != null)
                batch.setResalePrice(request.getResalePrice());
        }

        if (hasPaymentDetails(request)) {
            savePaymentDetails(batch.getId(), request);
            if ("Pending Payment".equalsIgnoreCase(batch.getStatus())) {
                batch.setStatus("Pending");
            }
        }

        return batchRepository.save(batch);
    }

    @Transactional
    public VendorActivationBatch approveBatch(Long batchId) {
        VendorActivationBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new EntityNotFoundException("Batch not found with id: " + batchId));

        batch.setStatus("Approved");
        batch.setIssuedAt(LocalDateTime.now());

        return batchRepository.save(batch);
    }

    public List<ApprovedBatchDropdownDto> getApprovedBatchesForDropdown(Long vendorId) {
        List<VendorActivationBatch> approvedBatches = batchRepository.findAll((root, query, cb) -> cb.and(
                cb.equal(root.get("vendorId"), vendorId),
                cb.equal(root.get("status"), "Approved"),
                cb.lessThan(root.get("usedActivations"), root.get("totalActivations"))));

        return approvedBatches.stream().map(batch -> {
            int pending = batch.getTotalActivations() - batch.getUsedActivations();
            String label = "Batch #" + batch.getId() + " - " + pending + " Licenses Available";
            return new ApprovedBatchDropdownDto(batch.getId(), batch.getLicenseModelId(), pending, label);
        }).collect(Collectors.toList());
    }

    @Transactional
    public ActivationKey issueLicense(IssueLicenseRequestDto request) {
        VendorActivationBatch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new EntityNotFoundException("Batch not found with id: " + request.getBatchId()));

        if (!"Approved".equalsIgnoreCase(batch.getStatus())) {
            throw new IllegalStateException("Batch is not approved yet.");
        }

        if (batch.getUsedActivations() >= batch.getTotalActivations()) {
            throw new IllegalStateException("Batch limit reached. No licenses left in this batch.");
        }

        // Check if tenant already has a valid license
        if (request.getRedeemedTenantId() != null) {

            List<ActivationKey> blockingKeys = activationKeyRepository
                    .findActiveBlockingKeys(request.getRedeemedTenantId(), LocalDateTime.now());

            if (!blockingKeys.isEmpty()) {
                throw new IllegalStateException(
                        "Tenant already has an active license. Cannot issue another key.");
            }
        }

        // Generate Readable Key: VND-XXXX-XXXX-XXXX
        String plainCode = "VND-" + generateRandomString(4) + "-" + generateRandomString(4) + "-"
                + generateRandomString(4);

        ActivationKey key = new ActivationKey();
        key.setVendorBatchId(batch.getId());
        // For hashing, usually we'd use BCrypt or similar. Using a simple placeholder
        // for now or standard java hash if needed.
        // Assuming BCrypt is provided by project or using simple representation for
        // demo.
        key.setActivationCodeHash(dummyHash(plainCode));
        key.setPlainCodeLast4(plainCode.substring(plainCode.length() - 4));
        key.setStatus(ActivationKey.Status.ISSUED);
        key.setIssuedToEmail(request.getIssuedToEmail());
        key.setIssuedToPhone(request.getIssuedToPhone());
        key.setRedeemedTenantId(request.getRedeemedTenantId());
        key.setExpiresAt(LocalDateTime.now().plusYears(1)); // Default 1 year expiry

        // Update Batch Used Count
        batch.setUsedActivations(batch.getUsedActivations() + 1);
        batchRepository.save(batch);

        ActivationKey savedKey = activationKeyRepository.save(key);

       
     // ---------------- FETCH VENDOR EMAIL PROPERLY ----------------
        String vendorEmail = vendorRepository.findById(request.getVendorId())
                .flatMap(vendor ->
                        userRepository.findById(vendor.getUserId())
                )
                .map(User::getEmail)
                .orElse(null); // CC optional

     // Build CC list (vendor email optional)
        List<String> ccEmails = null;

        if (vendorEmail != null && !vendorEmail.isBlank()) {
            ccEmails = List.of(vendorEmail);
        }

        // Send email
        if (request.getIssuedToEmail() != null) {
            emailService.sendLicenseIssuedEmail(
                    request.getIssuedToEmail(), // TO
                    ccEmails,                   // CC (nullable)
                    plainCode                   // Plain license key
            );
        }
        // Save vendor-tenant relationship
        VendorTenants vendorTenant = new VendorTenants();
        vendorTenant.setTenantId(request.getRedeemedTenantId());
        vendorTenant.setVendorId(request.getVendorId());
        vendorTenantsRepository.save(vendorTenant);

        // TODO: Send Email with plainCode to savedKey.getIssuedToEmail()
        System.out.println("Generated License Key: " + plainCode);

        return savedKey;
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private String dummyHash(String input) {
        return BCrypt.hashpw(input, BCrypt.gensalt());
        // return "HASHED_" + Integer.toHexString(input.hashCode());
    }

    @Transactional(readOnly = true)
    public VendorBatchResponseDto getBatchById(Long id) {

        VendorActivationBatch batch = batchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Batch not found with id: " + id));

        VendorBatchResponseDto dto = new VendorBatchResponseDto();
        BeanUtils.copyProperties(batch, dto);

        // ✅ vendor name (vendor → user → name)
        vendorRepository.findById(batch.getVendorId())
                .ifPresent(vendor -> userRepository.findById(vendor.getUserId())
                        .ifPresent(user -> dto.setVendorName(user.getName())));

        // ✅ payment details
        paymentRepository.findByBatchId(batch.getId())
                .ifPresent(payment -> {
                    dto.setPaymentMode(payment.getPaymentMode());
                    dto.setUtrTrnNo(payment.getUtrTrnNo());
                    dto.setRemark(payment.getRemark());
                    dto.setPaymentDate(payment.getPaymentDate());
                    dto.setImageUpload(payment.getImageUpload());
                });

        return dto;
    }

    private boolean hasPaymentDetails(VendorBatchRequestDto request) {
        return StringUtils.hasText(request.getPaymentMode()) ||
                StringUtils.hasText(request.getUtrTrnNo());
    }

    private void savePaymentDetails(Long batchId, VendorBatchRequestDto request) {
        VendorPaymentUpload payment = paymentRepository.findByBatchId(batchId)
                .orElse(new VendorPaymentUpload());

        payment.setBatchId(batchId);
        if (StringUtils.hasText(request.getPaymentMode()))
            payment.setPaymentMode(request.getPaymentMode());
        if (StringUtils.hasText(request.getUtrTrnNo()))
            payment.setUtrTrnNo(request.getUtrTrnNo());
        if (StringUtils.hasText(request.getRemark()))
            payment.setRemark(request.getRemark());
        if (StringUtils.hasText(request.getImageUpload()))
            payment.setImageUpload(request.getImageUpload());
        if (request.getPaymentDate() != null) {
            payment.setPaymentDate(request.getPaymentDate());
        }

        paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public Page<VendorBatchResponseDto> searchBatches(
            VendorBatchSearchRequestDto request,
            Long vendorId,
            Pageable pageable) {

        Specification<VendorActivationBatch> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (vendorId != null) {
                predicates.add(cb.equal(root.get("vendorId"), vendorId));
            }

            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getPlanId() != null) {
                predicates.add(cb.equal(root.get("licenseModelId"), request.getPlanId()));
            }

            if (request.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        request.getStartDate().atStartOfDay()));
            }

            if (request.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("createdAt"),
                        request.getEndDate().atTime(LocalTime.MAX)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<VendorActivationBatch> page = batchRepository.findAll(spec, pageable);

        return page.map(batch -> {

            VendorBatchResponseDto dto = new VendorBatchResponseDto();
            BeanUtils.copyProperties(batch, dto);

            // ✅ vendor name (vendorId → vendor table → name)
            if (batch.getVendorId() != null) {
                vendorRepository.findByUserId(batch.getVendorId())
                        .ifPresent(vendor -> dto.setVendorName(vendor.getName()));
            }

            // ✅ payment details
            paymentRepository.findByBatchId(batch.getId())
                    .ifPresent(payment -> {
                        dto.setPaymentMode(payment.getPaymentMode());
                        dto.setUtrTrnNo(payment.getUtrTrnNo());
                        dto.setRemark(payment.getRemark());
                    });

            return dto;
        });
    }

    public Page<ActivationKey> searchActivationKeys(ActivationKeySearchRequestDto request, Long vendorBatchId) {
        Pageable pageable = createPageable(request.getPage(), request.getSize(), request.getSortBy(),
                request.getSortDir());

        Specification<ActivationKey> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (vendorBatchId != null) {
                predicates.add(cb.equal(root.get("vendorBatchId"), vendorBatchId));
            }

            if (StringUtils.hasText(request.getStatus())) {
                try {
                    ActivationKey.Status statusEnum = ActivationKey.Status.valueOf(request.getStatus().toUpperCase());
                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) {
                }
            }

            if (StringUtils.hasText(request.getIssuedToEmail())) {
                predicates.add(cb.like(cb.lower(root.get("issuedToEmail")),
                        "%" + request.getIssuedToEmail().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(request.getIssuedToPhone())) {
                predicates.add(cb.like(root.get("issuedToPhone"), "%" + request.getIssuedToPhone() + "%"));
            }

            if (StringUtils.hasText(request.getPlainCodeLast4())) {
                predicates.add(cb.equal(root.get("plainCodeLast4"), request.getPlainCodeLast4()));
            }

            if (request.getExpiryStartDate() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("expiresAt"), request.getExpiryStartDate().atStartOfDay()));
            }
            if (request.getExpiryEndDate() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("expiresAt"), request.getExpiryEndDate().atTime(LocalTime.MAX)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return activationKeyRepository.findAll(spec, pageable);
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(sortBy);
        if ("desc".equalsIgnoreCase(sortDir)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        return PageRequest.of(page, size, sort);
    }

    /*
     * Get all Vendor Activation Batches with pagination
     */
    public Page<VendorActivationBatchResponseDTO> getAll(Pageable pageable) {
        return batchRepository.findAllBatches(pageable);
    }

    /**
     * Updates the status of a vendor activation batch.
     * 
     * Flow:
     * 1. Fetch batch by ID
     * 2. Update status received from frontend
     * 3. Save updated batch
     * 4. Return response details
     *
     * @param batchId Vendor activation batch ID
     * @param status  Status value sent from frontend
     */
    @Transactional
    public Map<String, Object> updateLicenseStatus(Long batchId, String status, Long userId) {

        // Fetch batch or throw exception if not found
        VendorActivationBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        // Update status based on frontend input
        batch.setStatus(status);

        // Set audit fields when status is "Approved" or "Rejected"
        if (("Approved".equalsIgnoreCase(status) || "Rejected".equalsIgnoreCase(status)) && userId != null) {
            batch.setIssuedAt(LocalDateTime.now());
            batch.setIssuedByUserId(userId);
        }

        // Persist updated batch
        batchRepository.save(batch);

        // Prepare API response
        Map<String, Object> response = new HashMap<>();
        response.put("batchId", batch.getId());
        response.put("status", batch.getStatus());
        response.put("message", "Batch status updated successfully");

        return response;
    }

}

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
 * Anjor         	 1.0.0       28-Dec-2025    class created
 *
 **/
package com.payvance.erp_saas.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vendor")
@Getter
@Setter
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // ================= RELATIONS =================

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "bank_details_id")
    private Long bankDetailsId;

    @Column(name = "vendor_discount_id")
    private Long vendorDiscountId;

    // ================= BASIC INFO =================

    @Column(name = "name")
    private String name;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "vendor_type")
    private String vendorType;

    @Column(name = "experience_years")
    private Integer experienceYears;

    // ================= LEGAL DETAILS =================

    @Column(name = "gst_no")
    private String gstNo;

    @Column(name = "cin_no")
    private String cinNo;

    @Column(name = "pan_no")
    private String panNo;

    @Column(name = "tan_no")
    private String tanNo;

    @Column(name = "aadhar_no")
    private String aadharNo;

    // ================= STATUS =================
    /**
     * draft
     * pending_approval
     * approved
     * rejected
     * disabled
     */
    @Column(name = "status")
    private String status;

    // ================= AUDIT =================

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "rejection_remark", length = 500)
    private String rejectionRemark;

    // ================= HELPERS =================

    public boolean isDraft() {
        return "draft".equalsIgnoreCase(status);
    }

    public boolean isPendingApproval() {
        return "pending_approval".equalsIgnoreCase(status);
    }

    public boolean isApproved() {
        return "approved".equalsIgnoreCase(status);
    }

    public boolean isRejected() {
        return "rejected".equalsIgnoreCase(status);
    }

    public boolean isDisabled() {
        return "disabled".equalsIgnoreCase(status);
    }
}

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

import java.time.LocalDateTime;

@Entity
@Table(
    name = "ca",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id")
    }
)
@Getter
@Setter
public class Ca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔑 One CA per user
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 🔗 Shared tables
    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "bank_details_id")
    private Long bankDetailsId;

    // 👤 CA profile
    @Column(name = "name")
    private String name;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    // 🧾 CA specific fields
    @Column(name = "ca_reg_no")
    private String caRegNo;

    @Column(name = "enrollment_year")
    private Integer enrollmentYear;

    @Column(name = "icai_member_status")
    private String icaiMemberStatus;

    @Column(name = "practice_type")
    private String practiceType;

    @Column(name = "firm_name")
    private String firmName;

    @Column(name = "icai_member_no")
    private String icaiMemberNo;

    @Column(name = "aadhar_no")
    private String aadharNo;
    
    @Column(name = "gst_no")
    private String gstNo;

    @Column(name = "cin_no")
    private String cinNo;

    @Column(name = "pan_no")
    private String panNo;

    @Column(name = "tan_no")
    private String tanNo;
    
    @Column(name = "ca_type")
    private String caType;

    // ⚙️ Status
    @Column(name = "status", length = 50)
    private String status;

    // ⏱ Audit fields (optional but recommended)
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "rejection_remark", length = 500)
    private String rejectionRemark;

    // ============================
    // LIFECYCLE HOOKS
    // ============================
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "active";
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ============================
    // HELPERS
    // ============================
    public boolean isActive() {
        return "active".equalsIgnoreCase(status);
    }
}

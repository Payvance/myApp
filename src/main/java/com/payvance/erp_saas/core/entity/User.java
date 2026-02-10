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
 * Anjor         	 1.0.0       26-Dec-2025    class created
 *
 **/
package com.payvance.erp_saas.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 150, unique = true, nullable = false)
    private String email;

    private String name;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_superadmin")
    private boolean isSuperadmin;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "rejection_remark", length = 500)
    private String rejectionRemark;

    // ================= MOBILE DEVICE TRACKING =================
    @Column(name = "mobile_device_id")
    private String mobileDeviceId;

    @Column(name = "mobile_fcm_token")
    private String mobileFcmToken;

    @Column(name = "mobile_platform") // android, ios
    private String mobilePlatform;

    @Column(name = "mobile_device_model")
    private String mobileDeviceModel;

    @Column(name = "mobile_os_version")
    private String mobileOsVersion;

    @Column(name = "mobile_app_version")
    private String mobileAppVersion;

    // ================= DESKTOP CONNECTOR TRACKING =================
    @Column(name = "desktop_device_id")
    private String desktopDeviceId;
}

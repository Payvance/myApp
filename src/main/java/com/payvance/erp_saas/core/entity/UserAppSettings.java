package com.payvance.erp_saas.core.entity;

import jakarta.persistence.*;
import lombok.*;
import com.payvance.erp_saas.core.enums.NotificationChannel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;
 
import com.payvance.erp_saas.core.convertor.NotificationChannelConverter;

@Entity
@Table(name = "user_app_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAppSettings {

    @Id
    private Long userId;

    @Column(nullable = false)
    private boolean pushEnabled = true;

    @Column(nullable = false)
    private boolean promosEnabled = true;

    @Convert(converter = NotificationChannelConverter.class)
    @Column(columnDefinition = "json", nullable = false)
    private Set<NotificationChannel> notificationChannels = new HashSet<>();

    @Column(nullable = false)
    private boolean locationEnabled = false;

    @Column(nullable = false)
    private boolean mobileDataEnabled = false;

    @Column(nullable = false)
    private boolean contactsEnabled = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
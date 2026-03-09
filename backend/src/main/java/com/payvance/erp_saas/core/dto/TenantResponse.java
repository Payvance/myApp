package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TenantResponse {

    private Long id;
    private String email;
    private String name;
    private String phone;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime trialStartAt;
    private LocalDateTime trialEndAt;
}

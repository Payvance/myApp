package com.payvance.erp_saas.core.dto;

import lombok.Builder;
import lombok.Data;
// Response DTO for profile information
@Data
@Builder
public class ProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String status;
}

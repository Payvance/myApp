package com.payvance.erp_saas.core.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaResponse {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String status;
}

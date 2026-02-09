package com.payvance.erp_saas.core.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VendorResponse {

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String status;
}
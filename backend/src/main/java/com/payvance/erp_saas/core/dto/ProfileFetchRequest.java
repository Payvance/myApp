package com.payvance.erp_saas.core.dto;

import lombok.Data;

@Data
public class ProfileFetchRequest {

    // Optional:
    // If provided → fetch single record
    // If null → fetch all records for role
    private Long userId;
}

package com.payvance.erp_saas.core.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class IssueLicenseRequestDto {

    @NotNull(message = "Batch ID is required")
    private Long batchId;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String issuedToEmail;

    @NotBlank(message = "Phone is required")
    @Size(min = 10, max = 15, message = "Phone must be between 10 and 15 digits")
    private String issuedToPhone;

    @NotNull(message = "Tenant ID is required")
    private Long redeemedTenantId;

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;
}

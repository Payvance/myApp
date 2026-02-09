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
 * Aniket Desai  	 1.0.0       06-Jan-2026    class created
 *
 **/
package com.payvance.erp_saas.core.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VendorBatchRequestDto {

    // Batch Details
    @NotNull(message = "Vendor ID is required")
    private Long vendorId;

    @NotNull(message = "Plan/License Model ID is required")
    private Long licenseModelId;

    private Integer vendorDiscountId;

    @NotNull(message = "Total Activations (Batch Size) is required")
    private Integer totalActivations;

    @NotNull(message = "Cost Price is required")
    private BigDecimal costPrice; // Assuming FE sends this or we calculate. Taking from FE for now as Plan entity
                                  // missing.

    @NotNull(message = "Resale Price is required")
    private BigDecimal resalePrice; // Assuming FE sends this.

    private String currency = "INR";

    // Optional Payment Details (Can be sent during creation or update)
    private String paymentMode;
    private LocalDate paymentDate;
    private String utrTrnNo;
    private String remark;
    private String imageUpload; // URL or Path
    // Valid status override? Usually not allowed for vendor.
}

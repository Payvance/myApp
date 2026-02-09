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

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class VendorBatchResponseDto {
    private Long id;
    private Long vendorId;
    private String vendorName;      // ✅ from vendor table
  
    private Long licenseModelId;
    private Integer vendorDiscountId;
    private Integer totalActivations;
    private Integer usedActivations;
    private BigDecimal costPrice;
    private BigDecimal resalePrice;
    private String currency;
    private String status;
    
    // ✅ payment details
    private String paymentMode;
    private LocalDate paymentDate;
    private String utrTrnNo;
    private String remark;
    private String imageUpload;
    private LocalDateTime issuedAt;
    private LocalDateTime createdAt;
	
}

package com.payvance.erp_saas.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * Data Transfer Object for Plan Limitations.
 *
 * @author Aniket Desai
 */
@Data
public class PlanLimitationDto {
    private Long id;

    @Min(value = 1, message = "Allowed user count must be at least 1")
    @JsonProperty("allowed_user_count")
    private Integer allowedUserCount = 1;

    @Min(value = 1, message = "Allowed company count must be at least 1")
    @JsonProperty("allowed_company_count")
    private Integer allowedCompanyCount = 1;

    @JsonProperty("allowed_user_count_till")
    private Integer allowedUserCountTill = 0;

    @JsonProperty("allowed_company_count_till")
    private Integer allowedCompanyCountTill = 0;
}

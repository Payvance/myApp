package com.payvance.erp_saas.erp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GroupDTO {
    private String guid;
    private String name;
    private String parentGuid;
    private String parentName;
    private String primaryGroup;
    private Boolean isRevenue;
    private Boolean isDeemedPositive;
    private Boolean isReserved;
    private Boolean affectsGrossProfit;
    private Integer sortPosition;
    private String companyGuid;
}

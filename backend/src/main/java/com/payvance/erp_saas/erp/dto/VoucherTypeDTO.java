package com.payvance.erp_saas.erp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VoucherTypeDTO {
    private String guid;
    private String name;
    @JsonProperty("numbering_method")
    private String numberingMethod;
    @JsonProperty("is_invoice")
    private Boolean isInvoice;
    @JsonProperty("is_optional")
    private Boolean isOptional;
    @JsonProperty("is_reserved")
    private Boolean isReserved;
    private String parent;
    @JsonProperty("closing_balance")
    private java.math.BigDecimal closingBalance;
    @JsonProperty("is_active")
    private Boolean active;
    private String companyGuid;
    private String companyName;
}

package com.payvance.erp_saas.erp.dto;

import lombok.Data;

@Data
public class StockItemDTO {
    private String guid;
    private String name;
    private String alias;
    private String stockGroupGuid;
    private String stockGroupName;
    private String categoryName;
    private String unitName;
    private String alternateUnit;
    private Double conversionFactor;
    private Double openingQuantity;
    private Double openingRate;
    private java.math.BigDecimal openingValue;
    private String gstHsnCode;
    private String gstTaxability;
    private Double gstRate;
    private Boolean isBatchwise;
    private Boolean isGodownTracking;
    private Boolean isReserved;

    private String costingMethod;
    private String valuationMethod;

    private Double inwardQuantity;
    private java.math.BigDecimal inwardValue;
    private Double outwardQuantity;
    private java.math.BigDecimal outwardValue;

    private Double closingQuantity;
    private Double closingRate;
    private java.math.BigDecimal closingValue;

    private String lastSaleDate;
    private String lastSaleParty;
    private Double lastSaleQuantity;
    private java.math.BigDecimal lastSalePrice;

    private String lastPurchaseDate;
    private String lastPurchaseParty;
    private Double lastPurchaseQuantity;
    private java.math.BigDecimal lastPurchasePrice;

    private Double igstRate;
    private Double cgstRate;
    private Double sgstRate;
    private Double cessRate;

    // Company context
    private String companyGuid;
    private String companyName;
}

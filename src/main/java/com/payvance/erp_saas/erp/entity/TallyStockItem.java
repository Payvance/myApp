package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tally_stock_items")
@Data
public class TallyStockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "guid", unique = true, nullable = false)
    private String guid;

    @Column(name = "name")
    private String name;

    @Column(name = "alias")
    private String alias;

    @Column(name = "part_no", length = 100)
    private String partNo;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "stock_group_guid")
    private String stockGroupGuid;

    @Column(name = "stock_group_name")
    private String stockGroupName;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "unit_name")
    private String unitName;

    @Column(name = "alternate_unit")
    private String alternateUnit;

    @Column(name = "conversion_factor")
    private Double conversionFactor;

    @Column(name = "opening_quantity")
    private Double openingQuantity;

    @Column(name = "opening_rate")
    private Double openingRate;

    @Column(name = "opening_value", precision = 19, scale = 4)
    private java.math.BigDecimal openingValue;

    @Column(name = "gst_hsn_code")
    private String gstHsnCode;

    @Lob
    @Column(name = "gst_hsn_description", columnDefinition = "TEXT")
    private String gstHsnDescription;

    @Column(name = "gst_taxability")
    private String gstTaxability;

    @Column(name = "gst_calculation_type")
    private String gstCalculationType;

    @Column(name = "gst_supply_type")
    private String gstSupplyType;

    @Column(name = "alter_id")
    private Long alterId;

    @Column(name = "gst_rate")
    private Double gstRate;

    @Column(name = "is_batchwise")
    private Boolean isBatchwise;

    @Column(name = "is_godown_tracking")
    private Boolean isGodownTracking;

    @Column(name = "is_reserved")
    private Boolean isReserved;

    @Column(name = "costing_method")
    private String costingMethod;

    @Column(name = "default_disc_rate", length = 50)
    private String defaultDiscountRate;

    // --- Behaviour Fields ---

    @Column(name = "ignore_physical_diff")
    private Boolean ignorePhysicalDifference;

    @Column(name = "ignore_negative_stock")
    private Boolean ignoreNegativeStock;

    @Column(name = "treat_sales_as_mfg")
    private Boolean treatSalesAsManufactured;

    @Column(name = "treat_purch_as_consumed")
    private Boolean treatPurchasesAsConsumed;

    @Column(name = "treat_rejects_as_scrap")
    private Boolean treatRejectsAsScrap;

    @Column(name = "valuation_method")
    private String valuationMethod;

    @Column(name = "inward_quantity")
    private Double inwardQuantity;

    @Column(name = "inward_value", precision = 19, scale = 4)
    private java.math.BigDecimal inwardValue;

    @Column(name = "outward_quantity")
    private Double outwardQuantity;

    @Column(name = "outward_value", precision = 19, scale = 4)
    private java.math.BigDecimal outwardValue;

    @Column(name = "closing_quantity")
    private Double closingQuantity;

    @Column(name = "closing_balance")
    private Double closingBalance;

    @Column(name = "closing_rate")
    private Double closingRate;

    @Column(name = "closing_value", precision = 19, scale = 4)
    private java.math.BigDecimal closingValue;

    @Column(name = "last_sale_date")
    private String lastSaleDate;

    @Column(name = "last_sale_party")
    private String lastSaleParty;

    @Column(name = "last_sale_quantity")
    private Double lastSaleQuantity;

    @Column(name = "last_sale_price", precision = 19, scale = 4)
    private java.math.BigDecimal lastSalePrice;

    @Column(name = "last_purchase_date")
    private String lastPurchaseDate;

    @Column(name = "last_purchase_party")
    private String lastPurchaseParty;

    @Column(name = "last_purchase_quantity")
    private Double lastPurchaseQuantity;

    @Column(name = "last_purchase_price", precision = 19, scale = 4)
    private java.math.BigDecimal lastPurchasePrice;

    @Column(name = "igst_rate")
    private Double igstRate;

    @Column(name = "cgst_rate")
    private Double cgstRate;

    @Column(name = "sgst_rate")
    private Double sgstRate;

    @Column(name = "cess_rate")
    private Double cessRate;

    @Column(name = "is_reverse_charge_applicable")
    private Boolean isReverseChargeApplicable;

    @Column(name = "gst_ineligible_itc")
    private Boolean gstIneligibleItc;
}

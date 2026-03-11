package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "tally_companies", schema = "erp")
@Data
public class TallyCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "guid", length = 100, nullable = false, unique = true)
    private String guid;

    @Column(name = "books_from")
    private String booksFrom;

    @Column(name = "gstin", length = 20)
    private String gstin;

    @Column(name = "timezone", length = 50)
    private String timezone;

    @Column(name = "financial_year")
    private Integer financialYear;

    @Column(name = "financial_year_from")
    private String financialYearFrom;

    @Column(name = "financial_year_to")
    private String financialYearTo;

    @Column(name = "pan", length = 20)
    private String pan;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "last_voucher_date")
    private LocalDate lastVoucherDate;

    @Column(name = "is_separate_actual_billed_quantity")
    private Boolean isSeparateActualBilledQty;

    @Column(name = "is_discount_applicable")
    private Boolean isDiscountApplicable;

    @Column(name = "is_cost_center_on")
    private Boolean isCostCenterOn;

    @Column(name = "is_inventory_on")
    private Boolean isInventoryOn;

    @Column(name = "is_accounting_on")
    private Boolean isAccountingOn;

    @Column(name = "is_payroll_on")
    private Boolean isPayrollOn;

    @Column(name = "cin_number", length = 50)
    private String cinNumber;

    @Column(name = "is_bill_wise_on")
    private Boolean isBillWiseOn;

    @Column(name = "is_batches_on")
    private Boolean isBatchesOn;

    @Column(name = "base_currency", length = 10)
    private String baseCurrency;
}

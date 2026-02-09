package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tally_voucher_types")
@Data
public class TallyVoucherType {

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

    @Column(name = "numbering_method")
    private String numberingMethod;

    @Column(name = "is_invoice")
    private Boolean isInvoice;

    @Column(name = "is_optional")
    private Boolean isOptional;

    @Column(name = "is_reserved")
    private Boolean isReserved;

    @Column(name = "parent")
    private String parent;

    @Column(name = "closing_balance", precision = 19, scale = 4)
    private java.math.BigDecimal closingBalance;

    @Column(name = "is_active")
    private Boolean active;
}

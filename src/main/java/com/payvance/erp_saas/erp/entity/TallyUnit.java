package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "tally_units")
@Data
public class TallyUnit {

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

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "decimal_places")
    private Integer decimalPlaces;

    @Column(name = "is_compound")
    private Boolean isCompound;

    @Column(name = "first_unit")
    private String firstUnit;

    @Column(name = "second_unit")
    private String secondUnit;

    @Column(name = "conversion", precision = 19, scale = 4)
    private BigDecimal conversion;
}

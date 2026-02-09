package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tally_stock_groups")
@Data
public class TallyStockGroup {

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

    @Column(name = "parent_guid")
    private String parentGuid;

    @Column(name = "parent_name")
    private String parentName;

    @Column(name = "is_reserved")
    private Boolean isReserved;
}

package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tally_masters")
@Data
public class Master {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guid", unique = true, nullable = false)
    private String guid;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "parent")
    private String parent;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
}

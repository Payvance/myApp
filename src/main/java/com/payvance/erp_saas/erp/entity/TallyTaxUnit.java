package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tally_tax_units")
@Data
public class TallyTaxUnit {

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

    @Column(name = "tax_type")
    private String taxType;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "gstin")
    private String gstin;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "address", length = 1000)
    private String address;

    @Column(name = "pin_code")
    private String pinCode;
}

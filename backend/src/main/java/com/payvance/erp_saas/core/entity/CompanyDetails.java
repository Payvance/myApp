package com.payvance.erp_saas.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "company_details")
@Getter
@Setter
public class CompanyDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenants;


    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    
    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "pin_code", length = 6)
    private String pinCode;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "gst_number", length = 15)
    private String gstNumber;


}
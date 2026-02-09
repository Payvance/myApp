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
}

package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "tally_configuration", schema = "erp")
@Data
public class TallyConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "host", length = 100)
    private String host;

    @Column(name = "port")
    private Integer port;

    @Column(name = "license_expiry_date", nullable = true)
    private LocalDate licenseExpiryDate;

    @Column(name = "license_serial_number", length = 50, nullable = false)
    private String licenseSerialNumber;

    @Column(name = "license_email", length = 150, nullable = true)
    private String licenseEmail;
}

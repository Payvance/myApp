package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hsn_sac_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HsnSac {

    @Id
    @Column(name = "code", length = 20)
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "hsn_sac")
    private Integer hsnSac; // 0 for SAC, 1 for HSN
}

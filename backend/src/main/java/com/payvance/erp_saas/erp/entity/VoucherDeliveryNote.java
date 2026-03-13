package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tally_voucher_delivery_notes")
@Data
public class VoucherDeliveryNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "note_number", length = 100)
    private String noteNumber;

    @Column(name = "note_date", columnDefinition = "DATE")
    private java.time.LocalDate noteDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;
}

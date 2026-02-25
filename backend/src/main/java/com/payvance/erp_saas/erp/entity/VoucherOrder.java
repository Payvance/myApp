package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tally_voucher_orders")
@Data
public class VoucherOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "basic_purchase_order_no", length = 100)
    private String basicPurchaseOrderNo;

    @Column(name = "basic_order_date", columnDefinition = "DATE")
    private java.time.LocalDate basicOrderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;
}

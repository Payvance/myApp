package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tally_batch_allocations")
@Data
public class TallyBatchAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "godown_name")
    private String godownName;

    @Column(name = "batch_name")
    private String batchName;

    @Column(name = "actual_qty")
    private String actualQty;

    @Column(name = "billed_qty")
    private String billedQty;

    @Column(name = "rate", precision = 19, scale = 4)
    private java.math.BigDecimal rate;

    @Column(name = "amount", precision = 19, scale = 4)
    private java.math.BigDecimal amount;

    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "indent_no")
    private String indentNo;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "batch_discount", precision = 19, scale = 4)
    private java.math.BigDecimal batchDiscount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_entry_id")
    private InventoryEntry inventoryEntry;
}

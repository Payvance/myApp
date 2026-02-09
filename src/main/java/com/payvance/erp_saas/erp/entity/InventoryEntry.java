package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tally_inventory_entries")
@Data
public class InventoryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_item_name")
    private String stockItemName;

    @Column(name = "billed_qty")
    private String billedQty;

    @Column(name = "actual_qty")
    private String actualQty;

    @Column(name = "ledger_name")
    private String ledgerName;

    @Column(name = "rate")
    private String rate;

    @Column(name = "amount", precision = 19, scale = 4)
    private java.math.BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @OneToMany(mappedBy = "inventoryEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<TallyBatchAllocation> batchAllocations;
}

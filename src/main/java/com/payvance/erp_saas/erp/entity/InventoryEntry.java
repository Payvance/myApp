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

    @Column(name = "stock_item_name", length = 200)
    private String stockItemName;

    @Column(name = "billed_qty", length = 50)
    private String billedQty;

    @Column(name = "actual_qty", length = 50)
    private String actualQty;

    @Column(name = "actual_qty_num", precision = 19, scale = 4)
    private java.math.BigDecimal actualQtyNum;

    @Column(name = "ledger_name", length = 200)
    private String ledgerName;

    @Column(name = "rate", precision = 19, scale = 4)
    private java.math.BigDecimal rate;

    @Column(name = "amount", precision = 19, scale = 4)
    private java.math.BigDecimal amount;

    @Column(name = "gst_rate", precision = 9, scale = 4)
    private java.math.BigDecimal gstRate;

    // --- Item Classification ---

    @Column(name = "hsn_code", length = 20)
    private String hsnCode;

    @Column(name = "gst_taxability", length = 50)
    private String gstTaxability;

    @Column(name = "uom", length = 20)
    private String uom;

    // --- Item-wise GST Amounts ---

    @Column(name = "cgst_amount", precision = 19, scale = 4)
    private java.math.BigDecimal cgstAmount;

    @Column(name = "sgst_amount", precision = 19, scale = 4)
    private java.math.BigDecimal sgstAmount;

    @Column(name = "igst_amount", precision = 19, scale = 4)
    private java.math.BigDecimal igstAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @OneToMany(mappedBy = "inventoryEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<TallyBatchAllocation> batchAllocations;
}

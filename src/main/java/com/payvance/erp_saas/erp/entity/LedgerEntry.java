package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tally_ledger_entries")
@Data
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ledger_name", length = 200)
    private String ledgerName;

    @Column(name = "amount", precision = 19, scale = 4)
    private java.math.BigDecimal amount;

    @Column(name = "is_debit")
    private Boolean isDebit;

    @Column(name = "is_party_ledger")
    private Boolean isPartyLedger;

    @Column(name = "method_type")
    private String methodType;

    // --- New Fields ---

    @Column(name = "gst_class")
    private String gstClass;

    @Column(name = "gst_nature", length = 50)
    private String gstNature;

    @Column(name = "cgst_rate", precision = 9, scale = 4)
    private java.math.BigDecimal cgstRate;

    @Column(name = "cgst_amount", precision = 19, scale = 4)
    private java.math.BigDecimal cgstAmount;

    @Column(name = "sgst_rate", precision = 9, scale = 4)
    private java.math.BigDecimal sgstRate;

    @Column(name = "sgst_amount", precision = 19, scale = 4)
    private java.math.BigDecimal sgstAmount;

    @Column(name = "igst_rate", precision = 9, scale = 4)
    private java.math.BigDecimal igstRate;

    @Column(name = "igst_amount", precision = 19, scale = 4)
    private java.math.BigDecimal igstAmount;

    // --- Classification ---

    @Column(name = "ledger_type", length = 50)
    private String ledgerType;

    @Column(name = "gst_duty_head", length = 50)
    private String gstDutyHead;

    // --- Cost Center Support ---

    @Column(name = "cost_center_name", length = 100)
    private String costCenterName;

    @Column(name = "cost_category_name", length = 100)
    private String costCategoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;
}

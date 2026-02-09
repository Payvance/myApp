package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "tally_vouchers")
@Data
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guid", unique = true, nullable = false)
    private String guid;

    @Column(name = "voucher_number")
    private String voucherNumber;

    @Column(name = "voucher_type")
    private String voucherType;

    @Column(name = "date")
    private java.time.LocalDate date;

    @Column(name = "amount", precision = 19, scale = 4)
    private java.math.BigDecimal amount;

    @Column(name = "narration", length = 1000)
    private String narration;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "alter_id")
    private Long alterId;

    @Column(name = "master_id")
    private Long masterId;

    @Column(name = "is_invoice")
    private Boolean isInvoice;

    @Column(name = "party_ledger_name")
    private String partyLedgerName;

    @Column(name = "delivery_notes")
    private String deliveryNotes;

    @Column(name = "payment_terms", length = 1000)
    private String paymentTerms;

    @Column(name = "consignee_name")
    private String consigneeName;

    @Column(name = "consignee_address", length = 1000)
    private String consigneeAddress;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LedgerEntry> ledgerEntries;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryEntry> inventoryEntries;
}

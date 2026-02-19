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

    @Column(name = "guid", unique = true, nullable = false, length = 50)
    private String guid;

    @Column(name = "voucher_number", length = 50)
    private String voucherNumber;

    @Column(name = "voucher_type", length = 50)
    private String voucherType;

    @Column(name = "date", columnDefinition = "DATE")
    private java.time.LocalDate date;

    @Column(name = "amount", precision = 19, scale = 4)
    private java.math.BigDecimal amount;

    @Lob
    @Column(name = "narration", columnDefinition = "TEXT")
    private String narration;

    @Column(name = "company_id", length = 50)
    private String companyId;

    @Column(name = "alter_id")
    private Long alterId;

    @Column(name = "master_id")
    private Long masterId;

    @Column(name = "is_invoice")
    private Boolean isInvoice;

    @Column(name = "party_ledger_name", length = 200)
    private String partyLedgerName;

    @Column(name = "delivery_notes", length = 500)
    private String deliveryNotes;

    @Lob
    @Column(name = "payment_terms", columnDefinition = "TEXT")
    private String paymentTerms;

    @Column(name = "consignee_name", length = 200)
    private String consigneeName;

    @Lob
    @Column(name = "consignee_address", columnDefinition = "TEXT")
    private String consigneeAddress;

    // --- New Fields ---

    @Column(name = "party_mailing_name", length = 200)
    private String partyMailingName;

    @Column(name = "party_pincode", length = 10)
    private String partyPinCode;

    @Column(name = "party_gst", length = 15)
    private String partyGst;

    @Column(name = "gst_registration_type", length = 50)
    private String gstRegistrationType;

    @Column(name = "place_of_supply", length = 100)
    private String placeOfSupply;

    @Column(name = "cmp_gst", length = 15)
    private String cmpGst;

    @Column(name = "cmp_state", length = 100)
    private String cmpState;

    @Column(name = "cmp_reg_type", length = 50)
    private String cmpRegType;

    @Column(name = "dispatch_name", length = 200)
    private String dispatchName;

    @Column(name = "dispatch_place", length = 100)
    private String dispatchPlace;

    @Column(name = "dispatch_state", length = 100)
    private String dispatchState;

    @Column(name = "dispatch_pin", length = 10)
    private String dispatchPin;

    @Column(name = "ship_place", length = 100)
    private String shipPlace;

    @Column(name = "bill_place", length = 100)
    private String billPlace;

    @Column(name = "irn", length = 100)
    private String irn;

    @Column(name = "irn_ack_date", nullable = true)
    private java.time.LocalDate irnAckDate;

    @Lob
    @Column(name = "irn_qr_code", columnDefinition = "TEXT")
    private String irnQrCode;

    @Lob
    @Column(name = "buyer_address", columnDefinition = "TEXT")
    private String buyerAddress;

    @Column(name = "voucher_category", length = 50)
    private String voucherCategory;

    @Column(name = "nature_of_voucher", length = 50)
    private String natureOfVoucher;

    @Column(name = "ack_no", length = 50)
    private String ackNo;

    @Column(name = "irp_source", length = 50)
    private String irpSource;

    @Column(name = "is_eway_applicable")
    private Boolean isEwayApplicable;

    @Column(name = "basic_buyer_name", length = 200)
    private String basicBuyerName;

    // --- Business Flags ---

    @Column(name = "is_cancelled")
    private Boolean isCancelled;

    @Column(name = "is_optional")
    private Boolean isOptional;

    @Column(name = "is_deleted_retained")
    private Boolean isDeletedRetained;

    @Column(name = "persisted_view", length = 50)
    private String persistedView;

    // --- Transport & E-Way Bill ---

    @Column(name = "vehicle_no", length = 20)
    private String vehicleNo;

    @Column(name = "transport_mode", length = 50)
    private String transportMode;

    @Column(name = "transport_distance")
    private Integer transportDistance;

    @Column(name = "eway_bill_no", length = 15)
    private String ewayBillNo;

    @Column(name = "eway_bill_valid_upto", length = 50)
    private String ewayBillValidUpto;

    // --- Financial Totals ---

    @Column(name = "taxable_amount", precision = 19, scale = 4)
    private java.math.BigDecimal taxableAmount;

    @Column(name = "cgst_amount", precision = 19, scale = 4)
    private java.math.BigDecimal cgstAmount;

    @Column(name = "sgst_amount", precision = 19, scale = 4)
    private java.math.BigDecimal sgstAmount;

    @Column(name = "igst_amount", precision = 19, scale = 4)
    private java.math.BigDecimal igstAmount;

    @Column(name = "round_off_amount", precision = 19, scale = 4)
    private java.math.BigDecimal roundOffAmount;

    @Column(name = "invoice_total", precision = 19, scale = 4)
    private java.math.BigDecimal invoiceTotal;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LedgerEntry> ledgerEntries;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryEntry> inventoryEntries;
}

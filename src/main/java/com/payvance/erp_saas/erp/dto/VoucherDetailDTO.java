package com.payvance.erp_saas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherDetailDTO {
    private Long id;
    private String guid;
    private String voucherNumber;
    private String voucherType;
    private LocalDate date;
    private String partyLedgerName;
    private BigDecimal totalAmount;
    private BigDecimal invoiceTotal; // Gross Total for display
    private Boolean isInvoice;

    private String narration;
    private String deliveryNotes;
    private String paymentTerms;
    private String consigneeName;
    private String consigneeAddress;
    private String buyerAddress;

    // Party Details
    private String partyGst;
    private String partyMailingName;
    private String partyPinCode;
    private String gstRegistrationType;
    private String placeOfSupply;
    private String basicBuyerName;

    // Company Details
    private String cmpGst;
    private String cmpState;
    private String cmpRegType;

    // Additional Addresses
    private String billPlace;

    // E-Invoice
    private String irn;
    private String ackNo;
    private LocalDate irnAckDate;
    private String irnQrCode;

    // E-Way Bill & Transport
    private String vehicleNo;
    private String transportMode;
    private Integer transportDistance;
    private String ewayBillNo;
    private String ewayBillValidUpto;

    // Address / Dispatch
    private String dispatchName;
    private String dispatchPlace;
    private String dispatchState;
    private String dispatchPin;
    private String shipPlace;

    // Financial Totals
    private BigDecimal taxableAmount;
    private BigDecimal cgstAmount;
    private BigDecimal sgstAmount;
    private BigDecimal igstAmount;
    private BigDecimal roundOffAmount;

    private List<VoucherInventoryDetailDTO> inventoryEntries;
    private List<VoucherLedgerDetailDTO> ledgerEntries;
}

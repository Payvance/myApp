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
    private Boolean isForex;
    private BigDecimal partyClosingBalance;

    private String narration;
    private String deliveryNotes;
    private String paymentTerms;
    private String consigneeName;
    private String consigneeAddress;
    private String consigneeMailingName;
    private String buyerAddress;

    // Party Details
    private String partyGst;
    private String partyMailingName;
    private String partyPinCode;
    private String gstRegistrationType;
    private String placeOfSupply;
    private String basicBuyerName;
    private String buyerPanNumber;
    private String partyStateName;
    private String partyCountryName;
    private String consigneePincode;
    private String consigneeStateName;
    private String consigneeCountryName;
    private String consigneePanNumber;

    // Company Details
    private String cmpGst;
    private String cmpState;
    private String cmpRegType;

    private String shippedBy;
    private String destinationCountry;
    private String placeOfReceipt;
    private String shipDocumentNo;
    private String portOfLoading;
    private String portOfDischarge;
    private String finalDestination;
    private String orderRef;
    private String shipVesselNo;
    private String buyersSalesTaxNo;
    private String dueDateOfPayment;
    private String serialNumInPla;
    private String dateTimeOfInvoice;
    private String dateTimeOfRemoval;
    private String mfgrAddressType;
    private String billOfLadingNo;
    private String billOfLadingDate;

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
    private List<VoucherOrderDetailDTO> orders;
    private List<VoucherEwayBillDTO> ewayBillDetails;
    private List<VoucherDeliveryNoteDTO> deliveryNotesList;

    private String voucherCategory;
    private String natureOfVoucher;
    private String irpSource;
    private Boolean isEwayApplicable;

    // Business Flags
    private Boolean isCancelled;
    private Boolean isOptional;
    private Boolean isDeletedRetained;
    private String persistedView;

}

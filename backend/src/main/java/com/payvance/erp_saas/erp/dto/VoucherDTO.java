package com.payvance.erp_saas.erp.dto;

import lombok.Data;
import java.util.List;

@Data
public class VoucherDTO {
    private String guid;
    private String remoteId;
    private String voucherType;
    private String voucherNumber;
    private String voucherDate;
    private String partyLedgerGuid;
    private String partyLedgerName;
    private Long alterId;
    private Long masterId;
    private java.math.BigDecimal totalAmount;
    private Boolean isInvoice;
    private Boolean isDeleted;
    private Boolean isForex;

    private String companyId;

    private List<VoucherInventoryDTO> inventoryEntries;
    private List<VoucherLedgerEntryDTO> ledgerEntries;

    private String narration;
    private String deliveryNotes;
    private String paymentTerms;
    private String consigneeName;
    private String consigneeAddress;

    // New Fields from XML
    private String partyMailingName;
    private String partyPinCode;
    private String partyGst;
    private String gstRegistrationType;
    private String placeOfSupply;
    private String cmpGst;
    private String cmpState;
    private String cmpRegType;
    private String dispatchName;
    private String dispatchPlace;
    private String dispatchState;
    private String dispatchPin;
    private String shipPlace;
    private String billPlace;
    private String irn;
    private String irnAckDate;
    private String irnQrCode;
    private String consigneeMailingName;
    private String buyerAddress;
    private String voucherCategory;
    private String natureOfVoucher;
    private String ackNo;
    private String irpSource;
    private Boolean isEwayApplicable;
    private String basicBuyerName;
    private String buyerPanNumber;
    private String partyStateName;
    private String partyCountryName;
    private String consigneePincode;
    private String consigneeStateName;
    private String consigneeCountryName;
    private String consigneePanNumber;

    // Business Flags
    private Boolean isCancelled;
    private Boolean isOptional;
    private Boolean isDeletedRetained;
    private String persistedView;

    // Transport & E-Way Bill
    private String vehicleNo;
    private String transportMode;
    private Integer transportDistance;
    private String ewayBillNo;
    private String ewayBillValidUpto;

    // Financial Totals
    private java.math.BigDecimal taxableAmount;
    private java.math.BigDecimal cgstAmount;
    private java.math.BigDecimal sgstAmount;
    private java.math.BigDecimal igstAmount;
    private java.math.BigDecimal roundOffAmount;
    private java.math.BigDecimal invoiceTotal;
    private java.util.List<VoucherOrderDTO> orders;
    private java.util.List<EwayBillDTO> ewayBillDetails;

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
}

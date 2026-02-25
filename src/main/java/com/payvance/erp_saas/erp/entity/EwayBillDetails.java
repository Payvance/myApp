package com.payvance.erp_saas.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "tally_eway_bill_details")
@Data
public class EwayBillDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_number", length = 50)
    private String billNumber;

    @Column(name = "bill_date")
    private LocalDate billDate;

    @Column(name = "document_type", length = 50)
    private String documentType;

    @Column(name = "sub_type", length = 50)
    private String subType;

    @Column(name = "consignor_name", length = 200)
    private String consignorName;

    @Column(name = "consignor_place", length = 100)
    private String consignorPlace;

    @Column(name = "consignor_pincode", length = 10)
    private String consignorPincode;

    @Lob
    @Column(name = "consignor_address", columnDefinition = "TEXT")
    private String consignorAddress;

    @Column(name = "consignee_name", length = 200)
    private String consigneeName;

    @Column(name = "consignee_place", length = 100)
    private String consigneePlace;

    @Column(name = "consignee_pincode", length = 10)
    private String consigneePincode;

    @Lob
    @Column(name = "consignee_address", columnDefinition = "TEXT")
    private String consigneeAddress;

    @Column(name = "shipped_from_state", length = 100)
    private String shippedFromState;

    @Column(name = "shipped_to_state", length = 100)
    private String shippedToState;

    @Column(name = "irp_source", length = 50)
    private String irpSource;

    @Column(name = "vehicle_number", length = 20)
    private String vehicleNumber;

    @Column(name = "transport_mode", length = 50)
    private String transportMode;

    @Column(name = "distance", length = 20)
    private String distance;

    @Column(name = "valid_upto")
    private LocalDate validUpto;

    @Column(name = "cancel_date")
    private LocalDate cancelDate;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;
}

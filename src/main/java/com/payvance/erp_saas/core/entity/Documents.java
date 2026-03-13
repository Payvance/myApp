package com.payvance.erp_saas.core.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class Documents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= RELATION WITH CA =================

    @OneToOne
    @JoinColumn(name = "ca_id")
    private Ca ca;

    // ================= RELATION WITH VENDOR =================

    @OneToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    // ================= DOCUMENTS =================

    @Lob
    @Column(name = "pan_document", columnDefinition = "LONGBLOB")
    private byte[] panDocument;

    @Lob
    @Column(name = "msme_document", columnDefinition = "LONGBLOB")
    private byte[] msmeDocument;

    @Lob
    @Column(name = "gst_certificate", columnDefinition = "LONGBLOB")
    private byte[] gstCertificate;

    // ================= AUDIT =================

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ================= LIFECYCLE =================

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
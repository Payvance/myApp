package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.VoucherDeliveryNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherDeliveryNoteRepository extends JpaRepository<VoucherDeliveryNote, Long> {
}

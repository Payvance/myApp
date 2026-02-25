package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.EwayBillDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EwayBillDetailsRepository extends JpaRepository<EwayBillDetails, Long> {
    List<EwayBillDetails> findByVoucherId(Long voucherId);
}

package com.payvance.erp_saas.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.payvance.erp_saas.core.entity.Documents;
import java.util.Optional;

@Repository
public interface DocumentsRepository extends JpaRepository<Documents, Long> {
    Optional<Documents> findByCaId(Long caId);

    Optional<Documents> findByVendorId(Long vendorId);
}

package com.payvance.erp_saas.core.repository;

import com.payvance.erp_saas.core.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByGatewayOrderId(String gatewayOrderId);
    Optional<Payment> findByInvoiceIdAndStatus(Long invoiceId, String status);
}

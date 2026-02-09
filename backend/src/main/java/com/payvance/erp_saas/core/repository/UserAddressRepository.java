package com.payvance.erp_saas.core.repository;

import com.payvance.erp_saas.core.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
// Repository for UserAddress entity with method to find by userId
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    Optional<UserAddress> findByUserId(Long userId);
}

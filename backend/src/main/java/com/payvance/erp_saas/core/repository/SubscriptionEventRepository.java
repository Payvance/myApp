package com.payvance.erp_saas.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.core.entity.SubscriptionEvent;

@Repository
public interface SubscriptionEventRepository extends JpaRepository<SubscriptionEvent, Long> {

}

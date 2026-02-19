package com.payvance.erp_saas.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.payvance.erp_saas.core.entity.AddOn;
import com.payvance.erp_saas.core.entity.SubscriptionAddon;

public interface SubscriptionAddonRepository extends JpaRepository<SubscriptionAddon, Long> {

	/*
	 * Fetch active addons for a given subscription ID
	 */
	@Query("""
	        SELECT a
	        FROM AddOn a
	        WHERE a.status = 'active'
	          AND a.id IN (
	              SELECT sa.addonId
	              FROM SubscriptionAddon sa
	              WHERE sa.subscriptionId = :subscriptionId
	                AND sa.status = 'active'
	          )
	    """)
	    List<AddOn> findActiveAddonsBySubscriptionId(@Param("subscriptionId") Long subscriptionId);

	List<SubscriptionAddon> findBySubscriptionId(Long subscriptionId);
}

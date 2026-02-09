package com.payvance.erp_saas.core.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payvance.erp_saas.core.entity.GstRate;

@Repository
public interface GstRateRepository extends JpaRepository<GstRate, Long> {

	/*
	 * Find the latest GST rate and its effective date on or before the given date.
	 */
	 @Query("""
		        SELECT new map(
		            g.rate as rate,
		            g.effectiveDate as effectiveDate
		        )
		        FROM GstRate g
		        WHERE g.effectiveDate <= :date
		        ORDER BY g.effectiveDate DESC
		    """)
		    List<Map<String, Object>> findLatestRateAndDate(@Param("date") LocalDate date);
}

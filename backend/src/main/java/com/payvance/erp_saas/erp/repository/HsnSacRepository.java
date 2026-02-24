package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.HsnSac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HsnSacRepository extends JpaRepository<HsnSac, String> {
    List<HsnSac> findByHsnSac(Integer hsnSac);
}

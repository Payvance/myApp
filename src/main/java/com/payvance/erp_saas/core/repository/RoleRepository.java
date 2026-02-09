/**
 * Copyright: Â© 2024 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of Payvance Innovation Pvt. Ltd.
 * Payvance Innovation Pvt. Ltd. retains all title to and intellectual property rights in these materials.
 *
 **/

/**
 *
 * @author           version     date        change description
 * Anjor         	 1.0.0       28-Dec-2025    class created
 *
 **/
package com.payvance.erp_saas.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payvance.erp_saas.core.entity.Role;

// import java.util.List;
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findById(Long id);

    Optional<Role> findByCode(String code);
}

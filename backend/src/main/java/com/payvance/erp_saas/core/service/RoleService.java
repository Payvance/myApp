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
 * om            	 1.0.0       05-Jan-2026    class created
 *
 **/
package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.dto.CodeValueResponse;
import com.payvance.erp_saas.core.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// service to fetch roles for dropdown
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<CodeValueResponse> getAllRolesForDropdown() {
        return roleRepository.findAll()
                .stream()
                .map(role -> new CodeValueResponse(
                        role.getId(),
                        role.getName()
                ))
                .toList();
    }
}

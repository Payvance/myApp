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
package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
 // DTO for code-value pair response
 // used in RoleController to return roles for dropdown
@Getter
@AllArgsConstructor
public class CodeValueResponse {

    private Long code;   // role.id
    private String value; // role.name
}

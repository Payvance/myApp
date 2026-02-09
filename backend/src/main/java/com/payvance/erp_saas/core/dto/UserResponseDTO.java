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
 * Aniiket.Desai 	 1.0.0       07-Jan-2026    class created
 *
 **/
package com.payvance.erp_saas.core.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private String name;
    private String email;
    private String phone;
}

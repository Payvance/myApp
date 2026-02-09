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
package com.payvance.erp_saas.core.enums;
// Enum representing different user roles in the system
public enum RoleEnum {

    SUPER_ADMIN(1),
    TENANT_ADMIN(2),
    TENANT_USER(3),
    VENDOR(4),
    CA(5);

    private final int id;

    RoleEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static RoleEnum fromId(Integer id) {
        if (id == null) return null;
        for (RoleEnum role : values()) {
            if (role.id == id) return role;
        }
        throw new IllegalArgumentException("Invalid role id: " + id);
    }
}

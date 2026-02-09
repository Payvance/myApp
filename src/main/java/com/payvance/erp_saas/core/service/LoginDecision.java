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
package com.payvance.erp_saas.core.service;

public record LoginDecision(
        boolean allowed,
        String redirectUrl,
        String message) {
    public static LoginDecision allow(String url) {
        return new LoginDecision(true, url, null);
    }

    public static LoginDecision deny(String msg) {
        return new LoginDecision(false, null, msg);
    }
}

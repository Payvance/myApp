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
 * Anjor         	 1.0.0       30-Dec-2025    class created
 *
 **/
package com.payvance.erp_saas.core.notification.sms;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class OtpStore {

    private static class OtpEntry {
        String otp;
        Instant expiresAt;

        OtpEntry(String otp, Instant expiresAt) {
            this.otp = otp;
            this.expiresAt = expiresAt;
        }
    }

    private final Map<String, OtpEntry> store = new ConcurrentHashMap<>();

    public void save(String mobile, String otp, int minutes) {
        store.put(
                mobile,
                new OtpEntry(otp, Instant.now().plusSeconds(minutes * 60)));
    }

    public boolean verify(String mobile, String otp) {
        OtpEntry entry = store.get(mobile);

        if (entry == null)
            return false;
        if (Instant.now().isAfter(entry.expiresAt)) {
            store.remove(mobile);
            return false;
        }

        boolean valid = entry.otp.equals(otp);
        if (valid) {
            store.remove(mobile); // one-time use
        }
        return valid;
    }
}

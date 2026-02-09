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
 * Anjor         	 1.0.0       29-Dec-2025    class created
 *
 **/
package com.payvance.erp_saas.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.trial")
@Data
public class TrialConfig {
    private int trialDays;
    private int extendedTrialDays;

    private boolean adsUnlockedEnabled;
    private int adsUnlockedDays;

    private int activeUsersCount;
    private int companiesCount;
}

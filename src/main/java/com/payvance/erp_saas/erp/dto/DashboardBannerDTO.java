package com.payvance.erp_saas.erp.dto;

import lombok.Data;

@Data
public class DashboardBannerDTO {
    private String title;
    private String subtitle;
    private String ctaText;
    private String discountBadge;
    private boolean isEnabled;
}

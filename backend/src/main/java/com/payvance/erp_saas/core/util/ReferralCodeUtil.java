package com.payvance.erp_saas.core.util;

public class ReferralCodeUtil {
	
	public static String buildCaReferralCode(Long caId, String caName) {

		 if (caName == null || caName.isBlank()) {
	            return "CA" + caId + "CA";
	        }

	        // Trim + split by spaces
	        String[] parts = caName.trim().split("\\s+");

	        // Take ONLY first word
	        String firstName = parts[0];

	        // Clean special characters & uppercase
	        firstName = firstName
	                .replaceAll("[^a-zA-Z0-9]", "")
	                .toUpperCase();

	        return "CA" + caId + firstName;
	    }

	public static String buildTenantReferralCode(Long tenantId, String tenantName) {
        if (tenantName == null || tenantName.isBlank()) {
            return "TA" + tenantId + "TA";
        }

        // Trim + split by spaces
        String[] parts = tenantName.trim().split("\\s+");

        // Take ONLY first word
        String firstName = parts[0];

        // Clean special characters & uppercase
        firstName = firstName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();

        return "TA" + tenantId + firstName;
    }
	

}

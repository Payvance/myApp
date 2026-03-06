package com.payvance.erp_saas.core.dto;

/**
 * DTO (Data Transfer Object) used to receive
 * Contact Us ID from the client request.
 * 
 * This is mainly used when fetching a specific
 * Contact Us record by ID through an API request.
 */

public class ContactUsIdRequest {

    // The ID of the contact message
    private Long id;

    /**
     * Getter method to retrieve the Contact Us ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Setter method to set the Contact Us ID
     */
    public void setId(Long id) {
        this.id = id;
    }
}
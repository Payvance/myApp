package com.payvance.erp_saas.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.payvance.erp_saas.core.dto.ContactUsIdRequest;
import com.payvance.erp_saas.core.dto.UpdateStatusRequest;
import com.payvance.erp_saas.core.entity.ContactUs;
import com.payvance.erp_saas.core.service.ContactUsService;

@RestController
// Base URL for all Contact Us APIs
@RequestMapping("/api/contact-us")
public class ContactUsController {

    // Service to handle Contact Us business logic
    @Autowired
    private ContactUsService contactUsService;

    /**
     * Save a new contact message and trigger Redmine issue creation and email
     * notification.
     * 
     * @param contactUs The contact message to save
     * @return The saved contact message
     */
    @PostMapping
    public ContactUs saveContactMessage(@RequestBody ContactUs contactUs) {
        return contactUsService.saveContactMessage(contactUs);
    }

    /**
     * Fetch all Contact Us messages with pagination support.
     * Pageable allows sorting, page size, and page number control.
     */
    @GetMapping("/get-all")
    public Page<ContactUs> getAllMessages(Pageable pageable) {
        return contactUsService.getAllMessages(pageable);
    }

    /**
     * Fetch a single contact message by its ID.
     */
    @PostMapping("/get-by-id")
    public ContactUs getContactById(@RequestBody ContactUsIdRequest request) {
        return contactUsService.getContactById(request.getId());
    }

    /**
     * Update the handle status of a Contact Us message.
     */
    @PostMapping("/update-status")
    public ContactUs updateStatus(@RequestBody UpdateStatusRequest request) {
        return contactUsService.updateHandleStatus(request.getId(), request.getStatus());
    }
}
package com.payvance.erp_saas.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.payvance.erp_saas.core.entity.ContactUs;
import com.payvance.erp_saas.core.enums.HandleStatus;
import com.payvance.erp_saas.core.repository.ContactUsRepository;

@Service
public class ContactUsService {

    // Repository to interact with the contact_us table
    @Autowired
    private ContactUsRepository contactUsRepository;

    // Service to create issues in Redmine
    @Autowired
    private RedmineService redmineService;

    // Service used to send email notifications
    @Autowired
    private EmailService emailService;

    /**
     * Saves a new contact message and triggers Redmine issue creation and email
     * notification.
     * 
     * @param contactUs The contact message to save
     * @return The saved contact message
     */
    public ContactUs saveContactMessage(ContactUs contactUs) {

        // Check if the message is new (ID will be null for new records)
        boolean isNew = contactUs.getId() == null;
        // Save the contact message in the database
        ContactUs savedContact = contactUsRepository.save(contactUs);

        // Create Redmine Issue only for new contact messages
        if (isNew) {
            redmineService.createIssue(savedContact);
            emailService.sendContactUsAdminNotificationEmail(savedContact);
        }

        return savedContact;
    }

    /**
     * Fetch all Contact Us messages with pagination support.
     * Pageable allows sorting, page size, and page number control.
     */
    public Page<ContactUs> getAllMessages(Pageable pageable) {
        return contactUsRepository.findAll(pageable);
    }

    /**
     * Fetch a single contact message by its ID.
     */
    public ContactUs getContactById(Long id) {
        return contactUsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact message not found with id: " + id));
    }

    /**
     * Update the handle status of a Contact Us message.
     * Example statuses: PENDING, RESOLVED.
     */
    public ContactUs updateHandleStatus(Long id, HandleStatus status) {

        // Fetch the contact message by ID
        ContactUs contact = contactUsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact message not found with id: " + id));

        // Update the handle status
        contact.setHandleStatus(status);

        // Save the updated contact message
        ContactUs savedContact = contactUsRepository.save(contact);

        return savedContact;
    }
}

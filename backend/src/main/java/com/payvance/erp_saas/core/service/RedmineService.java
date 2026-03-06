package com.payvance.erp_saas.core.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.payvance.erp_saas.core.entity.ContactUs;

@Service
public class RedmineService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${redmine.url}")
    private String redmineUrl;

    @Value("${redmine.api-key}")
    private String apiKey;

    @Value("${redmine.project-id}")
    private int projectId;

    @Value("${redmine.tracker-id}")
    private int trackerId;

    @Value("${redmine.priority-id}")
    private int priorityId;

    public void createIssue(ContactUs contact) {

        try {

            Map<String, Object> issue = new HashMap<>();

            issue.put("project_id", projectId);
            issue.put("tracker_id", trackerId);
            issue.put("subject", contact.getSubject());

            String description = "Name: " + contact.getFullName() + "\n" +
                    "Email: " + contact.getEmail() + "\n" +
                    "Phone: " + contact.getPhoneNumber() + "\n\n" +
                    "Message:\n" + contact.getMessage();

            issue.put("description", description);
            issue.put("priority_id", priorityId);

            Map<String, Object> payload = Map.of("issue", issue);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Redmine-API-Key", apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            restTemplate.postForEntity(
                    redmineUrl,
                    entity,
                    String.class);

            System.out.println("Redmine issue created successfully");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}

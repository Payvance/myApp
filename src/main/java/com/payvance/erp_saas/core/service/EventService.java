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
package com.payvance.erp_saas.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payvance.erp_saas.core.entity.Event;
import com.payvance.erp_saas.core.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void logEvent(String entityType, Long entityId, String eventType, Map<String, Object> payload, Long actorUserId) {
        String json = "{}";
        try {
            if (payload != null) {
                json = objectMapper.writeValueAsString(payload);
            }
        } catch (JsonProcessingException e) {
            // fallback to empty object
            json = "{}";
        }

        Event ev = new Event();
        ev.setEntityType(entityType);
        ev.setEntityId(entityId);
        ev.setEventType(eventType);
        ev.setPayload(json);
        ev.setActorUserId(actorUserId);

        eventRepository.save(ev);
    }
}

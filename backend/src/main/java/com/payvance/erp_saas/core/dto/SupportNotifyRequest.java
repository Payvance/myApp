package com.payvance.erp_saas.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// @Data // (Lombok annotation to generate getters, setters, toString, etc.)
public class SupportNotifyRequest {
    private Long ticket_id;
    private String message;
    private String category;
}

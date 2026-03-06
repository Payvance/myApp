package com.payvance.erp_saas.core.dto;

import com.payvance.erp_saas.core.enums.HandleStatus;

public class UpdateStatusRequest {

    private Long id;
    private HandleStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HandleStatus getStatus() {
        return status;
    }

    public void setStatus(HandleStatus status) {
        this.status = status;
    }
}
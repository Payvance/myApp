package com.payvance.erp_saas.core.dto;

import java.time.LocalDateTime;

/**
 * DTO for Tenant Management List response
 * 
 * @author system
 * @version 1.0.0
 */
public class TenantManagementListDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Integer isView;
    private LocalDateTime createdAt;

    public TenantManagementListDTO() {
        super();
    }

    public TenantManagementListDTO(Long id, String name, String email, String phone, Integer isView, LocalDateTime createdAt) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.isView = isView;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getIsView() {
        return isView;
    }

    public void setIsView(Integer isView) {
        this.isView = isView;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

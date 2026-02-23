package com.payvance.erp_saas.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserNestedData {

    private Long tenantId;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private Long roleId;
    private Boolean isactive;
    public UserNestedData(Long tenantId,Long userId,String name,String email,String phone,Long roleId,Boolean active) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.roleId = roleId;
        this.isactive = active;
    }
}
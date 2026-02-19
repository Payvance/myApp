package com.payvance.erp_saas.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RejectedUserDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Boolean active;
    private Integer roleId;
    private String roleName;
}

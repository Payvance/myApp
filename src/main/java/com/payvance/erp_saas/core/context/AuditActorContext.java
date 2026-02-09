package com.payvance.erp_saas.core.context;

import com.payvance.erp_saas.core.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuditActorContext {

    private Long tenantId;
    private Long userId;
    private RoleEnum role;
}


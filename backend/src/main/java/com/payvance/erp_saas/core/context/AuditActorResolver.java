package com.payvance.erp_saas.core.context;

import com.payvance.erp_saas.core.enums.RoleEnum;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditActorResolver {

    public static RoleEnum resolveRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        return RoleEnum.valueOf(
                auth.getAuthorities().iterator().next().getAuthority()
        );
    }

    public static Long resolveUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        return (Long) auth.getPrincipal();
    }
}

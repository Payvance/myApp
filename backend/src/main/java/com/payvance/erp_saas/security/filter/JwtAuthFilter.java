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
 * Anjor         	 1.0.0       26-Dec-2025    class created
 *
 **/
package com.payvance.erp_saas.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.payvance.erp_saas.security.util.JwtUtil;
import com.payvance.erp_saas.core.repository.PersonalAccessTokenRepository;
import com.payvance.erp_saas.core.repository.RoleRepository;
import com.payvance.erp_saas.core.entity.Role;
import com.payvance.erp_saas.erp.security.TenantContext;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwt;
    private final PersonalAccessTokenRepository patRepo;
    private final RoleRepository roleRepo;

    public JwtAuthFilter(
            JwtUtil jwt,
            PersonalAccessTokenRepository patRepo,
            RoleRepository roleRepo) {

        this.jwt = jwt;
        this.patRepo = patRepo;
        this.roleRepo = roleRepo;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔥 PREVENT CONTEXT OVERRIDE
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (!jwt.validateToken(token) || !jwt.isAccessToken(token)) {
                System.err.println("[SECURITY] Invalid or non-access token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }

            if (!patRepo.existsByTokenId(jwt.getTokenId(token))) {
                System.err.println("[SECURITY] Token revoked for ID: " + jwt.getTokenId(token));
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token revoked");
                return;
            }

            Long userId = jwt.getUserId(token);
            Long roleId = jwt.getRoleId(token);
            Long tenantId = jwt.getTenantId(token);

            if (tenantId != null) {
                TenantContext.setCurrentTenant(tenantId);
            }

            Role role = roleRepo.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found for ID: " + roleId));

            String authority = role.getCode();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority(authority)));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 🔎 DEBUG
            System.out.println("[SECURITY] Request URI: " + request.getRequestURI());
            System.out.println("[SECURITY] UserID: " + userId + ", Role: " + authority + ", TenantID: " + tenantId);

            try {
                filterChain.doFilter(request, response);
            } finally {
                TenantContext.clear();
            }

        } catch (Exception ex) {
            System.err.println("[SECURITY] Filter Exception: " + ex.getMessage());
            ex.printStackTrace();
            TenantContext.clear();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
        }
    }
}

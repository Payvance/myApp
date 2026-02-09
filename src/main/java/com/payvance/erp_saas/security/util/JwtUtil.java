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
package com.payvance.erp_saas.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.payvance.erp_saas.exceptions.BadCredentialsException;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenExpMs;
    private final long verifyEmailExpMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
            @Value("${jwt.access-exp-ms}") long accessTokenExpMs,
            @Value("${jwt.verify-email-exp-ms}") long verifyEmailExpMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpMs = accessTokenExpMs;
        this.verifyEmailExpMs = verifyEmailExpMs;
    }

    /* ===================== ACCESS TOKEN ===================== */
    public String generateAccessToken(Long userId, Long tenantId, Long roleId) {
        try {
            if (userId == null || roleId == null) {
                throw new IllegalArgumentException("userId or roleId cannot be null");
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("role_id", roleId);
            claims.put("type", "ACCESS");

            // tenantId is OPTIONAL (SUPERADMIN)
            if (tenantId != null) {
                claims.put("tenant_id", tenantId);
            }

            System.out.println("[DEBUG] JWT claims: " + claims);
            System.out.println("[DEBUG] accessTokenExpMs: " + accessTokenExpMs);
            System.out.println("[DEBUG] Signing key present: " + (key != null));

            return Jwts.builder()
                    .setSubject(String.valueOf(userId))
                    .setId(UUID.randomUUID().toString())
                    .addClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpMs))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

        } catch (Exception e) {
            System.err.println("[ERROR] JWT generation failed");
            e.printStackTrace();
            throw new RuntimeException("Failed to generate access token", e);
        }
    }

    /* ===================== EMAIL VERIFICATION TOKEN ===================== */
    public String generateEmailVerificationToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setId(UUID.randomUUID().toString())
                .addClaims(Map.of(
                        "email", email,
                        "type", "EMAIL_VERIFY"))
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(verifyEmailExpMs)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isEmailVerificationToken(String token) {
        return "EMAIL_VERIFY".equals(parseClaims(token).get("type", String.class));
    }

    public String getEmailFromVerificationToken(String token) {
        return parseClaims(token).get("email", String.class);
    }

    /* ===================== COMMON ===================== */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (BadCredentialsException ex) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        return "ACCESS".equals(parseClaims(token).get("type", String.class));
    }

    public Long getUserId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    public Long getTenantId(String token) {
        Claims claims = parseClaims(token);
        return claims.containsKey("tenant_id")
                ? claims.get("tenant_id", Long.class)
                : null;
    }

    public Long getRoleId(String token) {
        return parseClaims(token).get("role_id", Long.class);
    }

    public String getTokenId(String token) {
        return parseClaims(token).getId();
    }

    /** Returns token expiration as Instant, for DB storage */
    public Instant getExpiration(String token) {
        return parseClaims(token).getExpiration().toInstant();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw new BadCredentialsException("Token has expired");
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException("Invalid token");
        }
    }
}

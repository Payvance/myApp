package com.payvance.erp_saas.security.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.payvance.erp_saas.security.filter.JwtAuthFilter;
import com.payvance.erp_saas.security.permissions.ApiPermissions;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${app.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${app.cors.allow-credentials}")
    private boolean allowCredentials;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        /* ===================== PUBLIC ===================== */
                        .requestMatchers(ApiPermissions.PUBLIC_APIS)
                        .permitAll()

                        /* ===================== VENDOR ===================== */
                        .requestMatchers(ApiPermissions.VENDOR_APIS)
                        .hasAnyAuthority("VENDOR", "SUPER_ADMIN", "TENANT_ADMIN", "TENANT_USER", "CA")

                        /* ===================== TALLY ===================== */
                        .requestMatchers(ApiPermissions.TALLY_APIS)
                        .hasAnyAuthority("TENANT_ADMIN", "TENANT_USER")

                        /* ===================== SUPER ADMIN ===================== */
                        .requestMatchers(ApiPermissions.SUPER_ADMIN_APIS)
                        .hasAnyAuthority("VENDOR", "SUPER_ADMIN", "TENANT_ADMIN", "TENANT_USER", "CA")

                        /* ===================== TENANT ADMIN ===================== */

                        .requestMatchers(ApiPermissions.TENANT_ADMIN_APIS)
                        .hasAnyAuthority("VENDOR", "SUPER_ADMIN", "TENANT_ADMIN", "TENANT_USER", "CA")

                        /* ===================== TENANT USER ===================== */
                        .requestMatchers(ApiPermissions.TENANT_USER_APIS)
                        .hasAnyAuthority("VENDOR", "SUPER_ADMIN", "TENANT_ADMIN", "TENANT_USER", "CA")

                        /* ===================== CA ===================== */
                        .requestMatchers(ApiPermissions.CA_APIS)
                        .hasAnyAuthority("VENDOR", "SUPER_ADMIN", "TENANT_ADMIN", "TENANT_USER", "CA")

                        /* ===================== FALLBACK ===================== */
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /* ===================== AUTH ===================== */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of(allowedMethods.split(",")));

        if ("*".equals(allowedHeaders)) {
            config.addAllowedHeader("*");
        } else {
            config.setAllowedHeaders(List.of(allowedHeaders.split(",")));
        }

        config.setAllowCredentials(allowCredentials);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}

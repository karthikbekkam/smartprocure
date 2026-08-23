package com.smartprocure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // -------------------------------------------------
                // CSRF
                // -------------------------------------------------
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )

                // -------------------------------------------------
                // Exception Handling
                // -------------------------------------------------
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                )

                // -------------------------------------------------
                // SESSION MANAGEMENT
                // -------------------------------------------------
                // IMPORTANT:
                // Thymeleaf pages need browser authentication.
                // REST APIs continue to use JWT.
                // -------------------------------------------------
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // -------------------------------------------------
                // AUTHORIZATION
                // -------------------------------------------------
                .authorizeHttpRequests(authorize -> authorize

                        // =========================
                        // PUBLIC API
                        // =========================
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // =========================
                        // SWAGGER / OPENAPI
                        // =========================
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // =========================
                        // STATIC RESOURCES
                        // =========================
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()

                        // =========================
                        // PUBLIC WEB PAGES
                        // =========================
                        .requestMatchers(
                                "/",
                                "/about",
                                "/features",
                                "/solutions",
                                "/how-it-works",
                                "/contact",
                                "/login",
                                "/register",
                                "/forgot-password",
                                "/reset-password",
                                "/terms",
                                "/privacy",
                                "/error"
                        ).permitAll()

                        // =========================
                        // DASHBOARD & COMMON MVC PAGES
                        // =========================
                        .requestMatchers(
                                "/dashboard/**",
                                "/profile/**"
                        ).authenticated()

                        // =========================
                        // ROLE-SPECIFIC MVC PAGES
                        // =========================
                        .requestMatchers("/vendors/**").hasAnyRole("ADMIN", "PROCUREMENT_MANAGER", "FINANCE_MANAGER")
                        .requestMatchers("/products/**").hasAnyRole("ADMIN", "PROCUREMENT_MANAGER")
                        .requestMatchers("/inventory/**", "/warehouses/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers("/purchase-requests/**").hasAnyRole("ADMIN", "PROCUREMENT_MANAGER")
                        .requestMatchers("/purchase-orders/**").hasAnyRole("ADMIN", "PROCUREMENT_MANAGER", "FINANCE_MANAGER", "WAREHOUSE_MANAGER", "VENDOR")
                        .requestMatchers("/invoices/**").hasAnyRole("ADMIN", "FINANCE_MANAGER", "VENDOR")
                        .requestMatchers("/payments/**").hasAnyRole("ADMIN", "FINANCE_MANAGER")

                        // =========================
                        // VENDOR & ADMIN APIs
                        // =========================
                        .requestMatchers(
                                "/api/v1/vendors/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "PROCUREMENT_MANAGER",
                                "FINANCE_MANAGER"
                        )
                        .requestMatchers(
                                "/api/v1/users/**"
                        ).hasRole("ADMIN")

                        // =========================
                        // PROCUREMENT & PO APIs
                        // =========================
                        .requestMatchers(
                                "/api/v1/purchase-requests/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "PROCUREMENT_MANAGER"
                        )
                        .requestMatchers(
                                "/api/v1/purchase-orders/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "PROCUREMENT_MANAGER",
                                "FINANCE_MANAGER",
                                "WAREHOUSE_MANAGER",
                                "VENDOR"
                        )

                        // =========================
                        // WAREHOUSE APIs
                        // =========================
                        .requestMatchers(
                                "/api/v1/inventory/**",
                                "/api/v1/warehouses/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "WAREHOUSE_MANAGER"
                        )

                        // =========================
                        // FINANCE & VENDOR APIs
                        // =========================
                        .requestMatchers(
                                "/api/v1/invoices/**",
                                "/api/v1/payments/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "FINANCE_MANAGER",
                                "VENDOR"
                        )

                        // =========================
                        // ALL OTHER REQUESTS
                        // =========================
                        .anyRequest().authenticated()
                );

        // -------------------------------------------------
        // JWT FILTER
        // -------------------------------------------------
        // JWT remains active for REST/API authentication.
        // -------------------------------------------------
        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
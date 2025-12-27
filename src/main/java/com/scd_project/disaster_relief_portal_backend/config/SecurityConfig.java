package com.scd_project.disaster_relief_portal_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public signup endpoint
                        .requestMatchers("/api/auth/profile").permitAll()

                        // Admin endpoints (Spring looks for ROLE_ADMIN)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Volunteer endpoints (Spring looks for ROLE_VOLUNTEER)
                        .requestMatchers("/api/volunteer/**").hasRole("VOLUNTEER")

                        // Any other request just needs a valid token
                        .anyRequest().authenticated())
                .addFilterBefore(new FirebaseTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
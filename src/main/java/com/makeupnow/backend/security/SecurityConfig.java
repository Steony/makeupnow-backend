package com.makeupnow.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Désactive CSRF uniquement pour simplifier les tests
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/customers/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() // Autorise toutes les méthodes sur /customers
                .anyRequest().authenticated() // Authentification pour toute autre requête
            );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

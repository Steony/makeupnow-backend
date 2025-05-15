package com.makeupnow.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Désactive CSRF uniquement pour simplifier les tests
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/customers/**").permitAll() // Autorise toutes les requêtes sur /customers
                .anyRequest().authenticated() // Pour toute autre requête, authentification requise
            );

        return http.build();
    }
}

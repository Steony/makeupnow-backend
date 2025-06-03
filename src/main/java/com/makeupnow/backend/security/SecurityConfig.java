package com.makeupnow.backend.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity  // Active les @PreAuthorize
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1) Activer CORS en passant explicitement le CorsConfigurationSource
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 2) Désactiver CSRF (API stateless)
            .csrf(csrf -> csrf.disable())

            // 3) Session stateless (gestion du JWT sans session HTTP)
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 4) Définir les règles d'autorisation
            .authorizeHttpRequests(auth -> auth
                // 4.a) Autoriser toutes les requêtes OPTIONS vers /api/**
                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()

                // 4.b) Endpoints publics
                .requestMatchers(
                    "/api/users/register",
                    "/api/users/login",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()

                // 4.c) Routes réservées aux ADMIN
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                // 4.d) Toutes les autres requêtes nécessitent une authentification
                .anyRequest().authenticated()
            )

            // 5) Ajouter notre filtre JWT avant le filtre UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean pour encoder les mots de passe
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean pour l'AuthenticationManager (nécessaire pour l'authentification)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Bean CORS : définit qui peut appeler vos endpoints /api/**
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1) Origines autorisées : URL HTTP du bundler Metro (Expo)
        configuration.setAllowedOrigins(List.of(
            "http://192.168.1.185:8081",  // Expo/Metro sur votre PC
            "http://localhost:8081",        // (optionnel pour tests depuis navigateur)
            "http://192.168.1.157:8081"  // Expo/Metro sur votre téléphone
        ));

        // 2) Méthodes HTTP autorisées (inclure OPTIONS pour le pré-vol CORS)
        configuration.setAllowedMethods(List.of("OPTIONS", "GET", "POST", "PUT", "DELETE"));

        // 3) En-têtes HTTP autorisées (notamment Authorization pour le JWT)
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization"));

        // 4) Autoriser les credentials (pour transmettre le JWT dans l’en-tête)
        configuration.setAllowCredentials(true);

        // 5) Appliquer cette configuration aux routes /api/**
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}

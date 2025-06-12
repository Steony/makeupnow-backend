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
            // Activer CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Désactiver CSRF
            .csrf(csrf -> csrf.disable())

            // Session stateless
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Règles d'autorisation
            .authorizeHttpRequests(auth -> auth
                // Endpoints publics
                .requestMatchers(
                    "/api/users/register",
                    "/api/users/login",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()

                // Routes réservées à l'ADMIN
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMIN")

                // Routes accessibles uniquement aux PROVIDER et ADMIN (ex: gérer services, créneaux, etc.)
                .requestMatchers("/api/provider/**").hasAnyAuthority("ROLE_PROVIDER", "ROLE_ADMIN")
                .requestMatchers("/api/makeup-services/provider/**").hasAnyAuthority("ROLE_PROVIDER", "ROLE_ADMIN")
                .requestMatchers("/api/schedule/**").hasAnyAuthority("ROLE_PROVIDER", "ROLE_ADMIN")

                // Routes accessibles uniquement aux CUSTOMER et ADMIN (ex: réservations, avis)
                .requestMatchers("/api/customer/**").hasAnyAuthority("ROLE_CLIENT", "ROLE_ADMIN")
                .requestMatchers("/api/booking/**").hasAnyAuthority("ROLE_CLIENT", "ROLE_ADMIN")
                .requestMatchers("/api/reviews/**").hasAnyAuthority("ROLE_CLIENT", "ROLE_ADMIN")

                // Routes accessibles à tous les rôles connectés (customer, provider, admin)
                .requestMatchers("/api/makeup-services/**").hasAnyAuthority("ROLE_CLIENT", "ROLE_PROVIDER", "ROLE_ADMIN")

                // Autoriser les requêtes OPTIONS (CORS pre-flight)
                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()

                // Toute autre requête nécessite d'être connecté
                .anyRequest().authenticated()
            )

            // Ajout du filtre JWT
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Encodage des mots de passe
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager (pour l'authentification)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // CORS configuration pour Expo, navigateur, etc.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://192.168.1.185:8081",   // Expo/Metro sur PC
            "http://localhost:8081",       // Pour tests locaux
            "http://192.168.1.157:8081"    // Expo/Metro sur téléphone
        ));
        configuration.setAllowedMethods(List.of("OPTIONS", "GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}

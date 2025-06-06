package com.makeupnow.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
protected void doFilterInternal(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull FilterChain filterChain) throws ServletException, IOException {

    System.out.println("🔎 JwtAuthenticationFilter appelé pour: " + request.getServletPath());

    String authHeader = request.getHeader("Authorization");
    System.out.println("🔑 Authorization header: " + authHeader);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        System.out.println("🚫 Pas de Bearer token trouvé.");
        filterChain.doFilter(request, response);
        return;
    }

    String jwt = authHeader.substring(7);
    String userEmail;

    try {
        userEmail = jwtService.extractUsername(jwt);
        System.out.println("✅ userEmail extrait: " + userEmail);
    } catch (Exception e) {
        System.out.println("❌ Erreur extraction userEmail: " + e.getMessage());
        filterChain.doFilter(request, response);
        return;
    }

    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        var userDetails = customUserDetailsService.loadUserByUsername(userEmail);
        System.out.println("👤 UserDetails chargé: " + userDetails.getUsername());

        if (jwtService.isTokenValid(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
            System.out.println("🔐 Token JWT valide, Authentication créée.");

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println("🎯 Authentication injectée dans SecurityContext: " + authToken);
        } else {
            System.out.println("⚠️ JWT invalide !");
        }
    } else {
        System.out.println("🚫 userEmail null ou déjà authentifié");
    }

    filterChain.doFilter(request, response);


        // 🔄 Vérifie que l'utilisateur n'est pas déjà authentifié
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = customUserDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 🔁 Continue la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}

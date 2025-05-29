package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.dto.user.JwtResponseDTO;
import com.makeupnow.backend.dto.user.LoginRequestDTO;
import com.makeupnow.backend.dto.user.RegisterRequestDTO;
import com.makeupnow.backend.dto.user.UserUpdateDTO;
import com.makeupnow.backend.exception.InvalidRequestException;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.security.JwtService;
import com.makeupnow.backend.service.mysql.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    

    // ✅ Enregistrement
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequestDTO dto) {
        try {
            if (userService.existsByEmail(dto.getEmail())) {
                throw new InvalidRequestException("Un utilisateur avec cet email existe déjà.");
            }

            boolean created = userService.registerUser(
                    dto.getRole(),
                    dto.getFirstname(),
                    dto.getLastname(),
                    dto.getEmail(),
                    dto.getPassword(),
                    dto.getAddress(),
                    dto.getPhoneNumber()
            );

            if (created) {
                return ResponseEntity.ok("Utilisateur créé avec succès.");
            } else {
                throw new ResourceNotFoundException("Erreur lors de la création de l'utilisateur.");
            }

        } catch (Exception e) {
            e.printStackTrace(); // 📌 Pour debug console
            throw new InvalidRequestException("Erreur lors de l'inscription : " + e.getMessage());
        }
    }

    // ✅ Connexion avec génération de token JWT
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO request) {
        try {
            boolean success = userService.loginUser(request.getEmail(), request.getPassword());

            if (success) {
                User user = userService.findByEmail(request.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec cet email."));

                String token = jwtService.generateToken(user);
                return ResponseEntity.ok(new JwtResponseDTO(token));
            } else {
                throw new InvalidRequestException("Email ou mot de passe incorrect.");
            }

        } catch (Exception e) {
            e.printStackTrace(); // 📌 Debug console
            throw new InvalidRequestException("Erreur lors de la connexion : " + e.getMessage());
        }
    }

    // ✅ Déconnexion (stateless)
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        userService.logout(); // à étoffer si besoin
        return ResponseEntity.ok("Déconnexion réussie.");
    }

    // ✅ Mise à jour d’un utilisateur connecté
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@Valid @RequestBody UserUpdateDTO dto) {
        boolean updated = userService.updateUser(
                dto.getId(),
                dto.getFirstname(),
                dto.getLastname(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getAddress(),
                dto.getPhoneNumber()
        );

        if (updated) {
            return ResponseEntity.ok("Mise à jour réussie.");
        } else {
            throw new ResourceNotFoundException("Aucune modification effectuée.");
        }
    }
}

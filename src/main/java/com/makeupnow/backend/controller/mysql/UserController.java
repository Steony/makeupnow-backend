package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.dto.user.JwtResponseDTO;
import com.makeupnow.backend.dto.user.LoginRequestDTO;
import com.makeupnow.backend.dto.user.PasswordUpdateDTO;
import com.makeupnow.backend.dto.user.RegisterRequestDTO;
import com.makeupnow.backend.dto.user.UserUpdateDTO;
import com.makeupnow.backend.exception.InvalidRequestException;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.security.JwtService;
import com.makeupnow.backend.service.mysql.UserService;

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

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequestDTO dto) {
        if (userService.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body("Un utilisateur avec cet email existe déjà.");
        }
        try {
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
            e.printStackTrace();
            return ResponseEntity.status(400).body("Erreur lors de l'inscription : " + e.getMessage());
        }
    }

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
            e.printStackTrace();
            throw new InvalidRequestException("Erreur lors de la connexion : " + e.getMessage());
        }
    }
    
@PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        System.out.println(" Déconnexion appelée depuis le frontend !");
        userService.logout();
        return ResponseEntity.ok("Déconnexion réussie.");
    }

    
    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
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

    
     @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        boolean updated = userService.updatePassword(dto.getCurrentPassword(), dto.getNewPassword());
        if (updated) {
            return ResponseEntity.ok("Mot de passe mis à jour avec succès.");
        } else {
            return ResponseEntity.badRequest().body("Échec de la mise à jour du mot de passe.");
        }
    }

  
     @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));
        return ResponseEntity.ok(user);
    }
}

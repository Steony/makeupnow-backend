package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.dto.user.LoginRequestDTO;
import com.makeupnow.backend.dto.user.RegisterRequestDTO;
import com.makeupnow.backend.dto.user.UserUpdateDTO;
import com.makeupnow.backend.exception.InvalidRequestException;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.service.mysql.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Enregistrement
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequestDTO dto) {
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
    }

    // Connexion
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDTO request) {
        boolean success = userService.loginUser(request.getEmail(), request.getPassword());
        if (success) {
            return ResponseEntity.ok("Connexion réussie.");
        } else {
            throw new InvalidRequestException("Email ou mot de passe incorrect.");
        }
    }

    // Déconnexion
    @PostMapping("/logout")
public ResponseEntity<String> logout() {
    userService.logout(); // À étoffer selon ta stratégie future (JWT, session, etc.)
    return ResponseEntity.ok("Déconnexion réussie.");
}


    // Mise à jour des infos utilisateur
    @PutMapping("/update")
public ResponseEntity<String> updateUser(@RequestBody UserUpdateDTO dto) {
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

package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.exception.InvalidRequestException;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.service.mysql.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userService.existsByEmail(user.getEmail())) {
            throw new InvalidRequestException("Un utilisateur avec cet email existe déjà.");
        }
        boolean created = userService.registerUser(
            user.getRole(),
            user.getFirstname(),
            user.getLastname(),
            user.getEmail(),
            user.getPassword(),
            user.getAddress(),
            user.getPhoneNumber()
        );
        if (created) {
            return ResponseEntity.ok("Utilisateur créé avec succès.");
        } else {
            throw new ResourceNotFoundException("Erreur lors de la création de l'utilisateur.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        boolean success = userService.loginUser(user.getEmail(), user.getPassword());
        if (success) {
            return ResponseEntity.ok("Connexion réussie.");
        } else {
            throw new InvalidRequestException("Email ou mot de passe incorrect.");
        }
    }
}

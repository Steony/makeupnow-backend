package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.model.mysql.Customer;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.repository.mysql.UserRepository;
import com.makeupnow.backend.security.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserActionLogService userActionLogService;
    @Mock private LoginAttemptService loginAttemptService;
    @Mock private BCryptPasswordEncoder passwordEncoder; // Mock du passwordEncoder

    @InjectMocks private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExistsByEmail_ReturnsTrue() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean exists = userService.existsByEmail("test@example.com");

        assertTrue(exists);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void testLoginUser_Success() {
        // Création d'un utilisateur fictif
        Customer user = new Customer();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("$2a$10$hashed"); // Mot de passe déjà haché
        user.setActive(true);
        user.setRole(Role.CLIENT);

        // Préparation des mocks
        when(userRepository.findByEmailAndIsActiveTrue("test@example.com")).thenReturn(Optional.of(user));
        when(loginAttemptService.isBlocked("test@example.com")).thenReturn(false);
        when(passwordEncoder.matches("password", "$2a$10$hashed")).thenReturn(true);

        boolean result = userService.loginUser("test@example.com", "password");

        assertTrue(result);
        verify(userActionLogService).logActionByUserId(eq(1L), eq("Connexion"), eq("Connexion réussie"));
    }
}

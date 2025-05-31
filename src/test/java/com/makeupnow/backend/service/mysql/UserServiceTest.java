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
    @Mock private BCryptPasswordEncoder passwordEncoder;

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
        Customer user = new Customer();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("$2a$10$hashed");
        user.setActive(true);
        user.setRole(Role.CLIENT);

        when(userRepository.findByEmailAndIsActiveTrue("test@example.com")).thenReturn(Optional.of(user));
        when(loginAttemptService.isBlocked("test@example.com")).thenReturn(false);
        when(passwordEncoder.matches("password", "$2a$10$hashed")).thenReturn(true);

        boolean result = userService.loginUser("test@example.com", "password");

        assertTrue(result);
        verify(userActionLogService).logActionByUserId(eq(1L), eq("Connexion"), eq("Connexion réussie"));
    }

    @Test
    void testLoginUser_Failure_WrongPassword() {
        Customer user = new Customer();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("$2a$10$hashed");
        user.setActive(true);

        when(userRepository.findByEmailAndIsActiveTrue("test@example.com")).thenReturn(Optional.of(user));
        when(loginAttemptService.isBlocked("test@example.com")).thenReturn(false);
        when(passwordEncoder.matches("wrongPassword", "$2a$10$hashed")).thenReturn(false);

        boolean result = userService.loginUser("test@example.com", "wrongPassword");

        assertFalse(result);
        verify(userActionLogService).logActionByUserId(eq(1L), eq("Connexion"), contains("Échec de connexion"));
    }

    @Test
    void testLoginUser_Failure_AccountBlocked() {
        when(loginAttemptService.isBlocked("test@example.com")).thenReturn(true);

        Exception exception = assertThrows(SecurityException.class, () -> {
            userService.loginUser("test@example.com", "password");
        });

        assertTrue(exception.getMessage().contains("Trop de tentatives"));
    }

    @Test
    void testLoginUser_Failure_UserNotFound() {
        when(userRepository.findByEmailAndIsActiveTrue("unknown@example.com")).thenReturn(Optional.empty());
        when(loginAttemptService.isBlocked("unknown@example.com")).thenReturn(false);

        boolean result = userService.loginUser("unknown@example.com", "password");

        assertFalse(result);
        verify(loginAttemptService).loginFailed("unknown@example.com");
    }
}

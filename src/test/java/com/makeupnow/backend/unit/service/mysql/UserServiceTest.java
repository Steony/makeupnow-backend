package com.makeupnow.backend.unit.service.mysql;

import com.makeupnow.backend.model.mysql.Customer;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.repository.mysql.UserRepository;
import com.makeupnow.backend.security.LoginAttemptService;
import com.makeupnow.backend.service.mysql.UserActionLogService;
import com.makeupnow.backend.service.mysql.UserService;
import com.makeupnow.backend.unit.security.SecurityUtilsTestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
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

@Test
void testUpdateUser_Success() {
    // Arrange
    Long userId = 2L;
    Customer existingUser = new Customer();
    existingUser.setId(userId);
    existingUser.setFirstname("AncienPrenom");
    existingUser.setLastname("AncienNom");
    existingUser.setEmail("old@example.com");
    existingUser.setPassword(new BCryptPasswordEncoder().encode("oldpass"));
    existingUser.setAddress("Ancienne adresse");
    existingUser.setPhoneNumber("0102030405");
    existingUser.setActive(true);

    // 👉 Simule l'utilisateur connecté correspondant
    SecurityUtilsTestHelper.setAuthentication(userId, "old@example.com", Role.CLIENT);

    when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.of(existingUser));
    when(passwordEncoder.matches("newpass", existingUser.getPassword())).thenReturn(false);

    // Act
    boolean result = userService.updateUser(userId, "NouveauPrenom", "NouveauNom", "new@example.com",
            "newpass", "Nouvelle adresse", "0607080910");

    // Assert
    assertTrue(result, "La méthode doit retourner true.");
    assertEquals("NouveauPrenom", existingUser.getFirstname());
    assertEquals("NouveauNom", existingUser.getLastname());
    assertEquals("new@example.com", existingUser.getEmail());
    assertEquals("Nouvelle adresse", existingUser.getAddress());
    assertEquals("0607080910", existingUser.getPhoneNumber());
    verify(userRepository).save(existingUser);
    verify(userActionLogService).logActionByUserId(eq(userId), eq("Mise à jour du compte"), contains("Modifications"));

    // 🔴 Nettoyage (optionnel)
    SecurityUtilsTestHelper.clearAuthentication();
}

@Test
void testUpdateUser_UserNotFound() {
    Long userId = 99L;
    when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.empty());

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        userService.updateUser(userId, "a", "b", "c", "d", "e", "f");
    });

    assertTrue(exception.getMessage().contains("Utilisateur non trouvé"));
}

}

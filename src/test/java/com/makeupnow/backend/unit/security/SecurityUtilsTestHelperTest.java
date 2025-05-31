package com.makeupnow.backend.unit.security;

import com.makeupnow.backend.model.mysql.enums.Role;

import com.makeupnow.backend.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilsTestHelperTest {

    @AfterEach
    void tearDown() {
        // Réinitialise le contexte pour les tests suivants
        SecurityUtilsTestHelper.clearAuthentication();
    }

    @Test
    void testSetAuthentication_Success() {
        Long userId = 1L;
        String email = "test@example.com";
        Role role = Role.CLIENT;

        SecurityUtilsTestHelper.setAuthentication(userId, email, role);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication, "Le contexte doit contenir une authentification.");
        assertTrue(authentication.getPrincipal() instanceof CustomUserDetails, "Le principal doit être de type CustomUserDetails.");

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        assertEquals(userId, userDetails.getId());
        assertEquals(email, userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role.name())));
    }

    @Test
    void testClearAuthentication() {
        SecurityUtilsTestHelper.setAuthentication(1L, "test@example.com", Role.CLIENT);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        SecurityUtilsTestHelper.clearAuthentication();

        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Le contexte doit être vidé après clearAuthentication.");
    }
}

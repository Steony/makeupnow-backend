package com.makeupnow.backend.unit.security;


import com.makeupnow.backend.model.mysql.Customer;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.security.CustomUserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Helper pour simuler l'authentification dans les tests unitaires.
 */
public class SecurityUtilsTestHelper {

    /**
     * Configure le contexte de sécurité pour simuler un utilisateur connecté.
     *
     * @param userId l'identifiant de l'utilisateur
     * @param email  l'email de l'utilisateur
     * @param role   le rôle (ex: CLIENT, PROVIDER, ADMIN)
     */
    public static void setAuthentication(Long userId, String email, Role role) {
    // On simule un Customer car User est abstrait
    Customer fakeUser = new Customer();
    fakeUser.setId(userId);
    fakeUser.setEmail(email);
    fakeUser.setRole(role);
    fakeUser.setActive(true);

    CustomUserDetails userDetails = new CustomUserDetails(fakeUser);
    var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);
}


    /**
     * Réinitialise le contexte de sécurité (comme un logout).
     */
    public static void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }


@BeforeEach
    void setup() {
        SecurityUtilsTestHelper.setAuthentication(123L, "admin@email.com", Role.ADMIN);
    }

    @AfterEach
    void cleanup() {
        SecurityUtilsTestHelper.clearAuthentication();
    }

    @Test
    void testGetCurrentUserId() {
        assertEquals(123L, com.makeupnow.backend.security.SecurityUtils.getCurrentUserId());
    }

    @Test
    void testIsCurrentUserAdmin() {
        assertTrue(com.makeupnow.backend.security.SecurityUtils.isCurrentUserAdmin());
        assertFalse(com.makeupnow.backend.security.SecurityUtils.isCurrentUserProvider());
    }

}

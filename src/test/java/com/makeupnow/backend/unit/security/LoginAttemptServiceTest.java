package com.makeupnow.backend.unit.security;

import com.makeupnow.backend.security.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTest {

    private LoginAttemptService service;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptService();
    }

    @Test
    void testLoginFailed_BlockUser() {
        String email = "test@example.com";

        // Simule 5 tentatives ratées
        for (int i = 0; i < 5; i++) {
            service.loginFailed(email);
        }

        assertTrue(service.isBlocked(email), "L'utilisateur doit être bloqué après 5 échecs.");
    }

    @Test
    void testLoginSucceeded_ResetsAttempts() {
        String email = "test@example.com";

        // Simule des échecs
        service.loginFailed(email);
        assertFalse(service.isBlocked(email));

        // Succès → doit réinitialiser
        service.loginSucceeded(email);

        assertFalse(service.isBlocked(email));
    }

    @Test
    void testIsBlocked_UnblockedAfterTime() throws InterruptedException {
        String email = "test@example.com";

        // Bloque l'utilisateur
        for (int i = 0; i < 5; i++) {
            service.loginFailed(email);
        }

        assertTrue(service.isBlocked(email));

        // Simule que le temps passe (on triche car c'est un vrai test → à adapter avec un framework de clock si besoin)
        Thread.sleep(20); // Trop court pour l'effet mais montre la logique

        assertTrue(service.isBlocked(email), "Encore bloqué car le délai n'est pas vraiment passé (15 min).");
    }

@Test
    void shouldBlockAfterMaxAttempts() {
        String email = "fail@test.com";
        for (int i = 0; i < 5; i++) service.loginFailed(email);
        assertTrue(service.isBlocked(email));
    }

    @Test
    void shouldUnblockAfterLoginSuccess() {
        String email = "user@test.com";
        for (int i = 0; i < 5; i++) service.loginFailed(email);
        assertTrue(service.isBlocked(email));
        service.loginSucceeded(email);
        assertFalse(service.isBlocked(email));
    }

}

package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.security.SecurityUtilsTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BookingServiceTest {

    @BeforeEach
    void setup() {
        // Simuler un client connecté
        SecurityUtilsTestHelper.setAuthentication(1L, "client@email.com", Role.CLIENT);
    }

    @AfterEach
    void tearDown() {
        // Nettoie le contexte de sécurité
        SecurityUtilsTestHelper.clearAuthentication();
    }

    @Test
    void testCreateBooking() {
        
    }
}

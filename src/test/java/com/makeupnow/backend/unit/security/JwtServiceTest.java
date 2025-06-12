package com.makeupnow.backend.unit.security;

import com.makeupnow.backend.model.mysql.Customer;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.security.CustomUserDetails;
import com.makeupnow.backend.security.JwtService;

import io.jsonwebtoken.ExpiredJwtException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012"); // 32 chars for HS256
        ReflectionTestUtils.setField(jwtService, "expiration", 60000L); // 1 minute
    }

    @Test
void testGenerateAndValidateToken() {
    Customer user = new Customer(); // Customer hérite de User
    user.setId(1L);
    user.setEmail("test@test.com");
    user.setFirstname("Test");
    user.setRole(Role.CLIENT);

    String token = jwtService.generateToken(user);

    assertNotNull(token);
    assertEquals("test@test.com", jwtService.extractUsername(token));
    assertEquals("ROLE_CLIENT", jwtService.extractUserRole(token));
    assertEquals(1L, jwtService.extractUserId(token));
    assertTrue(jwtService.isTokenValid(token, new CustomUserDetails(user)));
}



    @Test
void testTokenExpired() throws InterruptedException {
    ReflectionTestUtils.setField(jwtService, "expiration", 1L);
    Provider user = Provider.builder().id(2L).email("expire@test.com").firstname("Expire").role(Role.PROVIDER).build();
    String token = jwtService.generateToken(user);
    Thread.sleep(10);
    assertThrows(ExpiredJwtException.class, () -> {
        jwtService.isTokenValid(token, new CustomUserDetails(user));
    });
}
}


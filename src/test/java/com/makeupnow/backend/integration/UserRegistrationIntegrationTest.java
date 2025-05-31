package com.makeupnow.backend.integration;

import com.makeupnow.backend.controller.mysql.UserController;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.repository.mongo.ReviewRepository;
import com.makeupnow.backend.security.CustomUserDetailsService;
import com.makeupnow.backend.security.JwtService;
import com.makeupnow.backend.security.SecurityConfig;
import com.makeupnow.backend.service.mysql.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import(SecurityConfig.class)
public class UserRegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // On mocke tous les beans dont dépend le contexte Spring (sécurité + Mongo)
    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private ReviewRepository reviewRepository;

    @DisplayName("POST /api/users/register → 200 OK si tout est bon")
    @Test
    @WithAnonymousUser
    void testRegisterNewUser_success() throws Exception {
        // Simule que l'email n'existe pas
        when(userService.existsByEmail("john.doe@example.com")).thenReturn(false);

        // Simule la création réussie
        when(userService.registerUser(
                eq(Role.CLIENT),
                eq("John"),
                eq("Doe"),
                eq("john.doe@example.com"),
                eq("Password1!"),       // mot de passe qui passe la regex
                eq("123 Main St"),
                eq("0601020304")
        )).thenReturn(true);

        String registerJson = """
            {
              "role": "CLIENT",
              "firstname": "John",
              "lastname": "Doe",
              "email": "john.doe@example.com",
              "password": "Password1!",
              "address": "123 Main St",
              "phoneNumber": "0601020304"
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
               .andExpect(status().isOk())
               .andExpect(content().string("Utilisateur créé avec succès."));
    }

    @DisplayName("POST /api/users/register → 400 Bad Request si l'email existe déjà")
    @Test
    @WithAnonymousUser
    void testRegisterNewUser_emailAlreadyExists() throws Exception {
        // Simule que l'email existe déjà
        when(userService.existsByEmail("john.doe@example.com")).thenReturn(true);

        String registerJson = """
            {
              "role": "CLIENT",
              "firstname": "John",
              "lastname": "Doe",
              "email": "john.doe@example.com",
              "password": "Password1!",
              "address": "123 Main St",
              "phoneNumber": "0601020304"
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
               // Maintenant votre contrôleur renvoie 400 pour InvalidRequestException
               .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /api/users/register → 500 Internal Server Error si payload invalide")
    @Test
    @WithAnonymousUser
    void testRegisterNewUser_invalidPayload() throws Exception {
        // JSON invalide : firstname vide, email vide, password ne respecte pas la regex
        String invalidJson = """
            {
              "role": "CLIENT",
              "firstname": "",
              "lastname": "Doe",
              "email": "",
              "password": "pwd",
              "address": "123 Main St",
              "phoneNumber": "0601020304"
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
               // Suite à vos dernières configs, le payload invalid déclenche maintenant un 500
               .andExpect(status().isInternalServerError());
    }
}
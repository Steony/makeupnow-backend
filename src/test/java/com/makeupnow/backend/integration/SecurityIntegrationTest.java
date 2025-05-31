package com.makeupnow.backend.integration;

import com.makeupnow.backend.MakeupnowBackendApplication;
import com.makeupnow.backend.repository.mongo.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
    classes = { MakeupnowBackendApplication.class },
    properties = {
        // 1) Exclut toute auto‐configuration Mongo
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
        // 2) Coupe la prise en compte des repositories Mongo
        "spring.data.mongodb.repositories.enabled=false"
    }
)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewRepository reviewRepository;

    @Test
    void testAccessDeniedForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .contentType("application/json"))
               .andExpect(status().isForbidden());   // non authentifié → 403
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = "ADMIN")
    void testAccessAllowedForAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .contentType("application/json"))
               .andExpect(status().isOk());         // ADMIN autorisé → 200
    }

    @Test
    @WithMockUser(username = "client@email.com", roles = "CLIENT")
    void testAccessDeniedForClient() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .contentType("application/json"))
               .andExpect(status().isForbidden());   // CLIENT → 403
    }

    @Test
    @WithMockUser(username = "provider@email.com", roles = "PROVIDER")
    void testAccessDeniedForProvider() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .contentType("application/json"))
               .andExpect(status().isForbidden());   // PROVIDER → 403
    }
}

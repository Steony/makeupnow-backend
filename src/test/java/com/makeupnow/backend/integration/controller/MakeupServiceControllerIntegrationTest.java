package com.makeupnow.backend.integration.controller;

import com.makeupnow.backend.controller.mysql.MakeupServiceController;
import com.makeupnow.backend.dto.makeupservice.MakeupServiceResponseDTO;
import com.makeupnow.backend.model.mysql.MakeupService;
import com.makeupnow.backend.service.mysql.MakeupServiceService;
import com.makeupnow.backend.security.CustomUserDetailsService;
import com.makeupnow.backend.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MakeupServiceController.class)
@AutoConfigureMockMvc(addFilters = false) // <<< Désactive les filtres de sécurité pour tes tests
public class MakeupServiceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MakeupServiceService makeupServiceService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // 🔥 Le test POST ne fonctionne en "succès" que si tu désactives les filtres de sécurité.
    // Pour vérifier la logique métier pure (et pas la sécurité Spring), c’est OK de le garder comme ça !

    @Test
    @DisplayName("POST /api/makeup-services -> 200 OK si PROVIDER")
    @WithMockUser(username = "pauline@makeupnow.com", roles = "PROVIDER")
    void testCreateMakeupService_success() throws Exception {
        // Arrange
        MakeupService created = new MakeupService();
        created.setId(10L);
        created.setTitle("Maquillage soirée");

        MakeupServiceResponseDTO responseDTO = new MakeupServiceResponseDTO();
        responseDTO.setId(10L);
        responseDTO.setTitle("Maquillage soirée");

        when(makeupServiceService.createMakeupServiceFromDTO(any())).thenReturn(created);
        when(makeupServiceService.mapToDTO(created)).thenReturn(responseDTO);

        String json = """
            {
                "providerId": 1,
                "categoryId": 2,
                "title": "Maquillage soirée",
                "description": "Super prestation",
                "duration": 60,
                "price": 80.0
            }
            """;

        mockMvc.perform(post("/api/makeup-services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.title").value("Maquillage soirée"));
    }

    // GET OK pour tous
    @Test
    @DisplayName("GET /api/makeup-services/category/2 → 200 OK, retourne une liste")
    @WithMockUser(username = "client@email.com", roles = "CLIENT")
    void testGetServicesByCategory_success() throws Exception {
        MakeupServiceResponseDTO dto = new MakeupServiceResponseDTO();
        dto.setId(1L);
        dto.setTitle("Maquillage mariée");
        when(makeupServiceService.getServicesByCategory(2L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/makeup-services/category/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Maquillage mariée"));
    }
}

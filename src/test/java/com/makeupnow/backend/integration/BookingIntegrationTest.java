package com.makeupnow.backend.integration;

import com.makeupnow.backend.controller.mysql.BookingController;
import com.makeupnow.backend.dto.booking.BookingResponseDTO;
import com.makeupnow.backend.model.mysql.enums.BookingStatus;
import com.makeupnow.backend.repository.mongo.ReviewRepository;
import com.makeupnow.backend.security.CustomUserDetailsService;
import com.makeupnow.backend.security.JwtService;
import com.makeupnow.backend.security.SecurityConfig;
import com.makeupnow.backend.service.mysql.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
@Import({ SecurityConfig.class }) 
public class BookingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @DisplayName("POST /api/bookings → 201, with totalPrice=100.0 & status=CONFIRMED")
    @Test
    @WithMockUser(username = "client@email.com", roles = "CLIENT")
    void testCreateBooking_returns201_andJsonPayload() throws Exception {
        BookingResponseDTO fakeResponse = new BookingResponseDTO();
        fakeResponse.setId(42L);
        fakeResponse.setDateBooking(LocalDateTime.of(2025, 1, 1, 12, 0));
        fakeResponse.setTotalPrice(100.0);
        fakeResponse.setStatus(BookingStatus.CONFIRMED);
        fakeResponse.setCustomerId(1L);
        fakeResponse.setProviderId(2L);
        fakeResponse.setServiceId(3L);
        fakeResponse.setScheduleId(4L);

        when(bookingService.createBooking(any())).thenReturn(fakeResponse);

        String bookingJson = """
            {
              "customerId": 1,
              "providerId": 2,
              "serviceId": 3,
              "scheduleId": 4,
              "totalPrice": 100.0
            }
            """;

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
               .andExpect(status().isCreated())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id").value(42))
               .andExpect(jsonPath("$.totalPrice").value(100.0))
               .andExpect(jsonPath("$.status").value("CONFIRMED"))
               .andExpect(jsonPath("$.customerId").value(1))
               .andExpect(jsonPath("$.providerId").value(2))
               .andExpect(jsonPath("$.serviceId").value(3))
               .andExpect(jsonPath("$.scheduleId").value(4));
    }
}

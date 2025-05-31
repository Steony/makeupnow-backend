package com.makeupnow.backend.controller.mysql;

@SpringBootTest
@ActiveProfiles("test") // Utilise application-test.yml
class BookingIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void createBooking_ShouldPersistInDatabase() {
        // Arrange
        BookingCreateDTO dto = new BookingCreateDTO();
        dto.setCustomerId(1L);
        dto.setProviderId(2L);
        // ...

        // Act
        Booking booking = bookingService.createBooking(dto);

        // Assert
        assertNotNull(booking.getId());
        assertTrue(bookingRepository.findById(booking.getId()).isPresent());
    }
}

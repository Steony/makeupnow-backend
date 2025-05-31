package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.dto.booking.BookingCreateDTO;
import com.makeupnow.backend.model.mysql.*;
import com.makeupnow.backend.model.mysql.enums.BookingStatus;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.repository.mysql.*;
import com.makeupnow.backend.security.SecurityUtilsTestHelper;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private MakeupServiceRepository makeupServiceRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private UserActionLogService userActionLogService;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // Simuler un client connecté
        SecurityUtilsTestHelper.setAuthentication(1L, "client@email.com", Role.CLIENT);
    }

    @AfterEach
    void tearDown() {
        // Nettoie le contexte de sécurité
        SecurityUtilsTestHelper.clearAuthentication();
    }

    @Test
    void testCreateBooking_Success() {
        // Préparer les données de test
        BookingCreateDTO dto = new BookingCreateDTO();
        dto.setCustomerId(1L);
        dto.setProviderId(2L);
        dto.setServiceId(3L);
        dto.setScheduleId(4L);
        dto.setTotalPrice(100.0);

        Customer customer = new Customer();
        customer.setId(1L);

        Provider provider = new Provider();
        provider.setId(2L);

        MakeupService service = new MakeupService();
        service.setId(3L);

        Schedule schedule = new Schedule();
        schedule.setId(4L);

        Booking bookingSaved = Booking.builder()
                .id(10L)
                .customer(customer)
                .provider(provider)
                .service(service)
                .schedule(schedule)
                .totalPrice(100.0)
                .status(BookingStatus.CONFIRMED)
                .build();

        // Définir le comportement des mocks
        when(bookingRepository.existsByScheduleIdAndStatusNot(4L, BookingStatus.CANCELLED)).thenReturn(false);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(providerRepository.findById(2L)).thenReturn(Optional.of(provider));
        when(makeupServiceRepository.findById(3L)).thenReturn(Optional.of(service));
        when(scheduleRepository.findById(4L)).thenReturn(Optional.of(schedule));
        when(bookingRepository.save(any())).thenReturn(bookingSaved);

        // Appeler la méthode à tester
        var response = bookingService.createBooking(dto);

        // Vérifications
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
        assertEquals(100.0, response.getTotalPrice());

        verify(bookingRepository).save(any(Booking.class));
        verify(userActionLogService).logActionByUserId(1L, "Création de réservation", "Réservation créée avec ID : 10");
    }
}

package com.makeupnow.backend.unit.service.mysql;

import com.makeupnow.backend.dto.booking.BookingCreateDTO;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mysql.*;
import com.makeupnow.backend.model.mysql.enums.BookingStatus;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.repository.mysql.*;
import com.makeupnow.backend.service.mysql.BookingService;
import com.makeupnow.backend.service.mysql.UserActionLogService;
import com.makeupnow.backend.unit.security.SecurityUtilsTestHelper;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
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
        SecurityUtilsTestHelper.clearAuthentication();
    }

    @Test
    void testCreateBooking_Success() {
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

        when(bookingRepository.existsByScheduleIdAndStatusNot(4L, BookingStatus.CANCELLED)).thenReturn(false);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(providerRepository.findById(2L)).thenReturn(Optional.of(provider));
        when(makeupServiceRepository.findById(3L)).thenReturn(Optional.of(service));
        when(scheduleRepository.findById(4L)).thenReturn(Optional.of(schedule));
        when(bookingRepository.save(any())).thenReturn(bookingSaved);

        var response = bookingService.createBooking(dto);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
        assertEquals(100.0, response.getTotalPrice());

        verify(bookingRepository).save(any(Booking.class));
        verify(userActionLogService).logActionByUserId(1L, "Création de réservation", "Réservation créée avec ID : 10");
    }

    @Test
    void testCreateBooking_FailsWhenScheduleAlreadyBooked() {
        BookingCreateDTO dto = new BookingCreateDTO();
        dto.setScheduleId(4L);

        when(bookingRepository.existsByScheduleIdAndStatusNot(4L, BookingStatus.CANCELLED)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.createBooking(dto);
        });

        assertEquals("Ce créneau est déjà réservé.", exception.getMessage());
        verify(bookingRepository, never()).save(any());
        verify(userActionLogService, never()).logActionByUserId(anyLong(), anyString(), anyString());
    }

    @Test
    void testCreateBooking_FailsWhenCustomerNotFound() {
        BookingCreateDTO dto = new BookingCreateDTO();
        dto.setCustomerId(1L);
        dto.setScheduleId(4L);

        when(bookingRepository.existsByScheduleIdAndStatusNot(4L, BookingStatus.CANCELLED)).thenReturn(false);
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.createBooking(dto);
        });

        assertEquals("Customer non trouvé avec l'id : 1", exception.getMessage());
        verify(bookingRepository, never()).save(any());
        verify(userActionLogService, never()).logActionByUserId(anyLong(), anyString(), anyString());
    }

    @Test
    void testCancelBooking_Success() {
        Long bookingId = 10L;

        Customer customer = new Customer();
        customer.setId(1L);

        Booking booking = Booking.builder()
                .id(bookingId)
                .status(BookingStatus.CONFIRMED)
                .customer(customer)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        bookingService.deleteBooking(bookingId);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        verify(bookingRepository).save(booking);
        verify(userActionLogService).logActionByUserId(
            anyLong(),
            eq("Annulation de réservation"),
            contains("Réservation ID")
        );
    }

    @Test
    void testCancelBooking_FailsWhenBookingNotFound() {
        Long bookingId = 10L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.deleteBooking(bookingId);
        });

        assertEquals("Booking non trouvé avec l'id : 10", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }
}

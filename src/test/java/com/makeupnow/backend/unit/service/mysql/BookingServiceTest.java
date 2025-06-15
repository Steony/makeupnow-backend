package com.makeupnow.backend.unit.service.mysql;

import com.makeupnow.backend.dto.booking.BookingCreateDTO;
import com.makeupnow.backend.dto.booking.BookingResponseDTO;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mysql.*;
import com.makeupnow.backend.model.mysql.enums.BookingStatus;
import com.makeupnow.backend.model.mysql.enums.PaymentStatus;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.repository.mongo.ReviewRepository;
import com.makeupnow.backend.repository.mysql.*;
import com.makeupnow.backend.service.mysql.BookingService;
import com.makeupnow.backend.service.mysql.UserActionLogService;
import com.makeupnow.backend.unit.security.SecurityUtilsTestHelper;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private ProviderRepository providerRepository;
    @Mock private MakeupServiceRepository makeupServiceRepository;
    @Mock private ScheduleRepository scheduleRepository;
    @Mock private UserActionLogService userActionLogService;
    @InjectMocks private BookingService bookingService;
    @Mock private ReviewRepository reviewRepository;
    @Mock private PaymentRepository paymentRepository;

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

        Customer customer = new Customer(); customer.setId(1L);
        Provider provider = new Provider(); provider.setId(2L);
        MakeupService service = new MakeupService(); service.setId(3L);
        Schedule schedule = new Schedule(); schedule.setId(4L);
        schedule.setStartTime(LocalDateTime.now().plusDays(1));

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
        Customer customer = new Customer(); customer.setId(1L);
        Booking booking = Booking.builder()
                .id(bookingId)
                .status(BookingStatus.CONFIRMED)
                .customer(customer)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        bookingService.cancelBooking(bookingId);

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
            bookingService.cancelBooking(bookingId);
        });
        assertEquals("Booking non trouvé avec l'id : 10", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCancelBooking_AccessDenied() {
        Long bookingId = 10L;
        Customer owner = new Customer(); owner.setId(1L);
        Booking booking = Booking.builder()
            .id(bookingId)
            .status(BookingStatus.CONFIRMED)
            .customer(owner)
            .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        // Simule un autre client (pas le proprio)
        SecurityUtilsTestHelper.setAuthentication(42L, "intrus@email.com", Role.CLIENT);
        assertThrows(AccessDeniedException.class, () -> bookingService.cancelBooking(bookingId));
        verify(bookingRepository, never()).save(any());
        verify(userActionLogService, never()).logActionByUserId(any(), any(), any());
    }

    @Test
    void testGetBookingsByCustomer_Success() {
        Long customerId = 1L;
        Customer customer = new Customer(); customer.setId(customerId);

        // Ajoute Schedule avec startTime !
        Schedule schedule = new Schedule();
        schedule.setId(50L);
        schedule.setStartTime(LocalDateTime.now().plusDays(2));

        // Ajoute aussi MakeupService, Provider bien remplis
        Provider provider = new Provider(); provider.setId(2L);
        MakeupService service = new MakeupService(); service.setId(3L);

        Booking booking = Booking.builder()
            .id(10L)
            .customer(customer)
            .provider(provider)
            .service(service)
            .schedule(schedule)
            .build();

        when(bookingRepository.findByCustomerId(customerId)).thenReturn(List.of(booking));
        List<BookingResponseDTO> result = bookingService.getBookingsByCustomer(customerId);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
    }

    @Test
    void testGetBookingsByCustomer_AccessDenied() {
        Long customerId = 999L; // pas le même que connecté
        SecurityUtilsTestHelper.setAuthentication(1L, "client@email.com", Role.CLIENT);
        assertThrows(AccessDeniedException.class, () -> bookingService.getBookingsByCustomer(customerId));
    }

    @Test
    void testGetBookingsByProvider_AsAdmin_Success() {
        Long providerId = 2L;
        SecurityUtilsTestHelper.setAuthentication(99L, "admin@email.com", Role.ADMIN);

        // Ajoute Schedule avec startTime
        Schedule schedule = new Schedule();
        schedule.setId(51L);
        schedule.setStartTime(LocalDateTime.now().plusDays(3));
        Provider provider = new Provider(); provider.setId(providerId);
        MakeupService service = new MakeupService(); service.setId(6L);
        Booking booking = Booking.builder()
            .id(11L)
            .provider(provider)
            .customer(new Customer())
            .service(service)
            .schedule(schedule)
            .build();

        when(bookingRepository.findByProviderId(providerId)).thenReturn(List.of(booking));
        List<BookingResponseDTO> result = bookingService.getBookingsByProvider(providerId);

        assertEquals(1, result.size());
        assertEquals(11L, result.get(0).getId());
    }

    @Test
    void testGetBookingsByProvider_AccessDenied() {
        Long providerId = 99L;
        SecurityUtilsTestHelper.setAuthentication(42L, "other@email.com", Role.PROVIDER);
        assertThrows(AccessDeniedException.class, () -> bookingService.getBookingsByProvider(providerId));
    }

    @Test
    void testGetAllBookings_AsAdmin() {
        SecurityUtilsTestHelper.setAuthentication(42L, "admin@email.com", Role.ADMIN);
        Schedule schedule = new Schedule();
        schedule.setId(123L);
        schedule.setStartTime(LocalDateTime.now().plusDays(1));
        Booking booking = Booking.builder()
            .id(10L)
            .customer(new Customer())
            .provider(new Provider())
            .service(new MakeupService())
            .schedule(schedule)
            .build();
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        List<BookingResponseDTO> result = bookingService.getAllBookings();
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
    }

    @Test
    void testUpdateBookingStatusIfPaymentsCompleted_BothPaid() {
        Long bookingId = 100L;
        Customer customer = new Customer(); customer.setId(1L);
        Provider provider = new Provider(); provider.setId(2L);
        // Ajoute Schedule avec startTime
        Schedule schedule = new Schedule();
        schedule.setId(42L);
        schedule.setStartTime(LocalDateTime.now().plusDays(5));
        Booking booking = Booking.builder()
                .id(bookingId)
                .customer(customer)
                .provider(provider)
                .schedule(schedule)
                .status(BookingStatus.CONFIRMED)
                .build();
        Payment paymentClient = Payment.builder()
                .booking(booking)
                .status(PaymentStatus.COMPLETED)
                .provider(provider)
                .build();
        paymentClient.getBooking().setCustomer(customer);

        Payment paymentProvider = Payment.builder()
                .booking(booking)
                .status(PaymentStatus.COMPLETED)
                .provider(provider)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(paymentRepository.findByBookingId(bookingId)).thenReturn(List.of(paymentClient, paymentProvider));
        assertEquals(BookingStatus.COMPLETED, booking.getStatus());
        verify(bookingRepository).save(booking);
    }
}

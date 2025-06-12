package com.makeupnow.backend.unit.service.mysql;

import com.makeupnow.backend.model.mysql.Booking;
import com.makeupnow.backend.model.mysql.Customer;
import com.makeupnow.backend.model.mysql.Payment;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.model.mysql.enums.PaymentStatus;
import com.makeupnow.backend.repository.mysql.BookingRepository;
import com.makeupnow.backend.repository.mysql.PaymentRepository;
import com.makeupnow.backend.repository.mysql.ProviderRepository;
import com.makeupnow.backend.service.mysql.PaymentService;
import com.makeupnow.backend.service.mysql.UserActionLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private UserActionLogService userActionLogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void confirmPaymentByCustomer_shouldConfirmPayment_whenCustomerIsAuthorized() {
        // Arrange
        Long paymentId = 1L;
        Long customerId = 100L;

        Customer customer = Customer.builder().id(customerId).build();
        Booking booking = Booking.builder().id(10L).customer(customer).build();
        Payment payment = Payment.builder().id(paymentId).status(PaymentStatus.PENDING).booking(booking).build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // Act
        //boolean result = paymentService.confirmPaymentByCustomer(paymentId, customerId);

        // Assert
        //assertTrue(result);
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
        verify(paymentRepository).save(payment);
        verify(userActionLogService).logActionByUserId(
                eq(customerId),
                eq("Confirmation paiement"),
                contains("Paiement confirmé")
        );
    }

    /* 

    @Test
    void confirmPaymentByCustomer_shouldThrowSecurityException_whenCustomerIsNotAuthorized() {
        // Arrange
        Long paymentId = 1L;
        Long customerId = 100L;
        Long otherCustomerId = 999L; // autre client qui essaie

        Customer customer = Customer.builder().id(customerId).build();
        Booking booking = Booking.builder().id(10L).customer(customer).build();
        Payment payment = Payment.builder().id(paymentId).status(PaymentStatus.PENDING).booking(booking).build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // Act + Assert
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            paymentService.confirmPaymentByCustomer(paymentId, otherCustomerId);
        });

        assertEquals("Accès refusé pour ce client", exception.getMessage());
        assertEquals(PaymentStatus.PENDING, payment.getStatus()); // pas modifié
        verify(paymentRepository, never()).save(any());
        verify(userActionLogService).logActionByUserId(
                eq(otherCustomerId),
                eq("Échec confirmation paiement"),
                contains("Client non autorisé")
        );
    }*/

@Test
void confirmPaymentByProvider_shouldConfirmPayment_whenProviderIsAuthorized() {
    // Arrange
    Long paymentId = 1L;
    Long providerId = 200L;

    Provider provider = Provider.builder().id(providerId).build();
    Payment payment = Payment.builder()
            .id(paymentId)
            .status(PaymentStatus.PENDING)
            .provider(provider)
            .booking(Booking.builder().id(10L).build()) // tu peux adapter si besoin
            .build();

    when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

    // Act
    boolean result = paymentService.confirmPaymentByProvider(paymentId, providerId);

    // Assert
    assertTrue(result);
    assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
    verify(paymentRepository).save(payment);
    verify(userActionLogService).logActionByUserId(
            eq(providerId),
            eq("Confirmation paiement"),
            contains("Paiement confirmé")
    );
}

@Test
void confirmPaymentByProvider_shouldThrowSecurityException_whenProviderIsNotAuthorized() {
    // Arrange
    Long paymentId = 1L;
    Long providerId = 200L;
    Long otherProviderId = 999L; // autre prestataire qui essaie

    Provider provider = Provider.builder().id(providerId).build();
    Payment payment = Payment.builder()
            .id(paymentId)
            .status(PaymentStatus.PENDING)
            .provider(provider)
            .booking(Booking.builder().id(10L).build())
            .build();

    when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

    // Act + Assert
    SecurityException exception = assertThrows(SecurityException.class, () -> {
        paymentService.confirmPaymentByProvider(paymentId, otherProviderId);
    });

    assertEquals("Accès refusé pour ce prestataire", exception.getMessage());
    assertEquals(PaymentStatus.PENDING, payment.getStatus()); // pas modifié
    verify(paymentRepository, never()).save(any());
    verify(userActionLogService).logActionByUserId(
            eq(otherProviderId),
            eq("Échec confirmation paiement"),
            contains("Prestataire non autorisé")
    );
}
}


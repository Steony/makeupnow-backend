package com.makeupnow.backend.unit.service.mysql;

import com.makeupnow.backend.dto.payment.PaymentCreateDTO;
import com.makeupnow.backend.dto.payment.PaymentResponseDTO;
import com.makeupnow.backend.dto.payment.PaymentStatusUpdateDTO;
import com.makeupnow.backend.exception.ResourceNotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
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
        eq("Paiement reçu par le prestataire"),
        contains("Paiement")
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
   verify(userActionLogService, never()).logActionByUserId(any(), any(), any());

}


@Test
void createPayment_shouldSucceed_whenBookingAndProviderExist() {
    // Arrange
    PaymentCreateDTO dto = new PaymentCreateDTO();
    dto.setBookingId(1L);
    dto.setProviderId(2L);
    dto.setAmount(80.0);
    dto.setStatus(PaymentStatus.PENDING);

    Booking booking = Booking.builder().id(1L).customer(Customer.builder().id(42L).build()).build();
    Provider provider = Provider.builder().id(2L).build();

    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
    when(providerRepository.findById(2L)).thenReturn(Optional.of(provider));
    when(paymentRepository.save(any())).thenAnswer(inv -> {
        Payment p = inv.getArgument(0);
        p.setId(10L);
        return p;
    });

    // Act
    PaymentResponseDTO result = paymentService.createPayment(dto);

    // Assert
    assertNotNull(result);
    assertEquals(10L, result.getId());
    verify(paymentRepository).save(any());
    verify(bookingRepository).save(any());
    verify(userActionLogService).logActionByUserId(
        eq(42L), eq("Création paiement"), contains("Paiement créé avec ID"));
}

@Test
void createPayment_shouldThrow_whenBookingNotFound() {
    PaymentCreateDTO dto = new PaymentCreateDTO();
    dto.setBookingId(999L);
    dto.setProviderId(2L);
    when(bookingRepository.findById(999L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> paymentService.createPayment(dto));
}

@Test
void updatePaymentStatus_shouldSucceed_whenAdminUpdates() {
    PaymentStatusUpdateDTO dto = new PaymentStatusUpdateDTO();
    dto.setPaymentId(1L);
    dto.setStatus(PaymentStatus.COMPLETED);

    Payment payment = Payment.builder().id(1L).status(PaymentStatus.PENDING).provider(new Provider()).booking(Booking.builder().id(123L).build()).build();

    when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
    when(paymentRepository.save(any())).thenReturn(payment);

    boolean result = paymentService.updatePaymentStatus(dto);
    assertTrue(result);
    assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
    verify(paymentRepository).save(payment);
    verify(userActionLogService).logActionByUserId(any(), eq("Mise à jour paiement"), contains("Statut du paiement ID 1"));
}

@Test
void updatePaymentStatus_shouldThrow_whenPaymentNotFound() {
    PaymentStatusUpdateDTO dto = new PaymentStatusUpdateDTO();
    dto.setPaymentId(99L);
    dto.setStatus(PaymentStatus.COMPLETED);
    when(paymentRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> paymentService.updatePaymentStatus(dto));
}



}


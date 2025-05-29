package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.dto.payment.PaymentCreateDTO;
import com.makeupnow.backend.dto.payment.PaymentResponseDTO;
import com.makeupnow.backend.dto.payment.PaymentStatusUpdateDTO;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mysql.Payment;
import com.makeupnow.backend.model.mysql.enums.PaymentStatus;
import com.makeupnow.backend.repository.mysql.PaymentRepository;
import com.makeupnow.backend.repository.mysql.BookingRepository;
import com.makeupnow.backend.repository.mysql.ProviderRepository;
import com.makeupnow.backend.security.SecurityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private UserActionLogService userActionLogService;

    @PreAuthorize("hasRole('CLIENT')")
    @Transactional
    public PaymentResponseDTO createPayment(PaymentCreateDTO dto) {
        var booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'id : " + dto.getBookingId()));

        var provider = providerRepository.findById(dto.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Prestataire non trouvé avec l'id : " + dto.getProviderId()));

        Payment payment = Payment.builder()
                .amount(dto.getAmount())
                .status(dto.getStatus())
                .booking(booking)
                .provider(provider)
                .build();

        Payment saved = paymentRepository.save(payment);

        userActionLogService.logActionByUserId(
                booking.getCustomer().getId(),
                "Création paiement",
                "Paiement créé avec ID " + saved.getId() + " pour la réservation " + booking.getId()
        );

        return mapToDTO(saved);
    }

    @PreAuthorize("hasRole('CLIENT)")
    @Transactional
    public boolean confirmPaymentByCustomer(Long paymentId, Long customerId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé avec l'id : " + paymentId));

        if (!payment.getBooking().getCustomer().getId().equals(customerId)) {
            userActionLogService.logActionByUserId(
                    customerId,
                    "Échec confirmation paiement",
                    "Client non autorisé à confirmer le paiement ID " + paymentId
            );
            throw new SecurityException("Accès refusé pour ce client");
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        userActionLogService.logActionByUserId(
                customerId,
                "Confirmation paiement",
                "Paiement confirmé avec ID " + paymentId
        );

        return true;
    }

    @PreAuthorize("hasRole('PROVIDER')")
    @Transactional
    public boolean confirmPaymentByProvider(Long paymentId, Long providerId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé avec l'id : " + paymentId));

        if (!payment.getProvider().getId().equals(providerId)) {
            userActionLogService.logActionByUserId(
                    providerId,
                    "Échec confirmation paiement",
                    "Prestataire non autorisé à confirmer le paiement ID " + paymentId
            );
            throw new SecurityException("Accès refusé pour ce prestataire");
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        userActionLogService.logActionByUserId(
                providerId,
                "Confirmation paiement",
                "Paiement confirmé avec ID " + paymentId
        );

        return true;
    }

    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
public List<PaymentResponseDTO> getPaymentsByCustomer(Long customerId) {
    Long currentUserId = SecurityUtils.getCurrentUserId();
    String currentRole = SecurityUtils.getCurrentUserRole();

    if (!"ADMIN".equals(currentRole) && !currentUserId.equals(customerId)) {
        throw new AccessDeniedException("Accès refusé à ces paiements.");
    }

    return paymentRepository.findByBookingCustomerId(customerId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
}


    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
public List<PaymentResponseDTO> getPaymentsByProvider(Long providerId) {
    Long currentUserId = SecurityUtils.getCurrentUserId();
    String currentRole = SecurityUtils.getCurrentUserRole();

    if (!"ADMIN".equals(currentRole) && !currentUserId.equals(providerId)) {
        throw new AccessDeniedException("Accès refusé à ces paiements.");
    }

    return paymentRepository.findByBookingProviderId(providerId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
}


    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

   @PreAuthorize("hasRole('ADMIN')")
@Transactional
public boolean updatePaymentStatus(PaymentStatusUpdateDTO dto) {
    Payment payment = paymentRepository.findById(dto.getPaymentId())
            .orElseThrow(() -> new ResourceNotFoundException("Payment non trouvé avec l'id : " + dto.getPaymentId()));

    payment.setStatus(dto.getStatus());
    paymentRepository.save(payment);

    Long adminId = SecurityUtils.getCurrentUserId(); // 🔐 sécurisé

    userActionLogService.logActionByUserId(
        adminId,
        "Mise à jour paiement",
        "Statut du paiement ID " + dto.getPaymentId() + " mis à jour à " + dto.getStatus().name()
    );

    return true;
}



    private PaymentResponseDTO mapToDTO(Payment payment) {
    return PaymentResponseDTO.builder()
            .id(payment.getId())
            .amount(payment.getAmount())
            .status(payment.getStatus().name()) // 
            .paymentDate(payment.getPaymentDate())
            .bookingId(payment.getBooking().getId())
            .providerId(payment.getProvider().getId())
            .build();
}

}
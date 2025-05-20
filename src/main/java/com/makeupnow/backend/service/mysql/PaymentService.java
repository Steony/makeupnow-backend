package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mysql.Payment;
import com.makeupnow.backend.model.mysql.enums.PaymentStatus;
import com.makeupnow.backend.repository.mysql.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserActionLogService userActionLogService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public Payment createPayment(Long bookingId, double amount, PaymentStatus status) {
        Payment payment = Payment.builder()
                .amount(amount)
                .status(status)
                .booking(new com.makeupnow.backend.model.mysql.Booking())
                .build();
        payment.getBooking().setId(bookingId);
        Payment savedPayment = paymentRepository.save(payment);

        // Log création paiement
        userActionLogService.logActionByUserId(
            savedPayment.getBooking().getCustomer().getId(),
            "Création paiement",
            "Paiement créé avec ID " + savedPayment.getId() + " pour la réservation " + bookingId
        );

        return savedPayment;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public boolean confirmPaymentByCustomer(Long paymentId, Long customerId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment non trouvé avec l'id : " + paymentId));
        if (!payment.getBooking().getCustomer().getId().equals(customerId)) {
            // Log tentative échec accès
            userActionLogService.logActionByUserId(
                customerId,
                "Echec confirmation paiement",
                "Tentative non autorisée de confirmer le paiement ID " + paymentId
            );
            throw new SecurityException("Accès refusé pour ce client");
        }
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        // Log confirmation paiement
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
                .orElseThrow(() -> new ResourceNotFoundException("Payment non trouvé avec l'id : " + paymentId));
        if (!payment.getBooking().getProvider().getId().equals(providerId)) {
            // Log tentative échec accès
            userActionLogService.logActionByUserId(
                providerId,
                "Echec confirmation paiement",
                "Tentative non autorisée de confirmer le paiement ID " + paymentId
            );
            throw new SecurityException("Accès refusé pour ce prestataire");
        }
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        // Log confirmation paiement
        userActionLogService.logActionByUserId(
            providerId,
            "Confirmation paiement",
            "Paiement confirmé avec ID " + paymentId
        );

        return true;
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public List<Payment> getPaymentsByCustomer(Long customerId) {
        return paymentRepository.findByBookingCustomerId(customerId);
    }

    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    public List<Payment> getPaymentsByProvider(Long providerId) {
        return paymentRepository.findByBookingProviderId(providerId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public boolean updatePaymentStatus(Long paymentId, PaymentStatus status, Long adminId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment non trouvé avec l'id : " + paymentId));
        payment.setStatus(status);
        paymentRepository.save(payment);

        // Log mise à jour paiement par admin
        userActionLogService.logActionByUserId(
            adminId,
            "Mise à jour paiement",
            "Statut du paiement ID " + paymentId + " mis à jour à " + status.name()
        );

        return true;
    }
}

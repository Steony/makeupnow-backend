package com.makeupnow.backend.dto.payment;

import com.makeupnow.backend.model.mysql.enums.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PaymentCreateDTO {

    @NotNull(message = "L'identifiant de la réservation est requis.")
    private Long bookingId;

    @NotNull(message = "L'identifiant du prestataire est requis.")
    private Long providerId;

    @Positive(message = "Le montant doit être positif.")
    private double amount;

    @NotNull(message = "Le statut du paiement est requis.")
    private PaymentStatus status;
}
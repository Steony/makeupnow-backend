package com.makeupnow.backend.dto.payment;

import com.makeupnow.backend.model.mysql.enums.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PaymentStatusUpdateDTO {

    @NotNull(message = "L'identifiant du paiement est requis.")
    private Long paymentId;

    @NotNull(message = "Le nouveau statut est requis.")
    private PaymentStatus status;
}
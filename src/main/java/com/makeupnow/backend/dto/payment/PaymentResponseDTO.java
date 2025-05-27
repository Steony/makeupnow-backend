package com.makeupnow.backend.dto.payment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponseDTO {
    private Long id;
    private double amount;
    private String status; 
    private LocalDateTime paymentDate;
    private Long bookingId;
    private Long providerId;
}

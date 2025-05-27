package com.makeupnow.backend.dto.payment;

import com.makeupnow.backend.model.mysql.enums.PaymentStatus;
import lombok.Data;

@Data
public class PaymentCreateDTO {
    private Long bookingId;
    private Long providerId; 
    private double amount;
    private PaymentStatus status;
}

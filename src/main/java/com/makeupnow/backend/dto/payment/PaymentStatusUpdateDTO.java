package com.makeupnow.backend.dto.payment;

import com.makeupnow.backend.model.mysql.enums.PaymentStatus;
import lombok.Data;

@Data
public class PaymentStatusUpdateDTO {
    private Long paymentId;
    private PaymentStatus status;
    private Long adminId;
}

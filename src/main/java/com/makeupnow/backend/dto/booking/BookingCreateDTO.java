package com.makeupnow.backend.dto.booking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingCreateDTO {
    private Long customerId;
    private Long providerId;
    private Long serviceId;
    private Long scheduleId;
    private double totalPrice;
}

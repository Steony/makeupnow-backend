package com.makeupnow.backend.dto.booking;

import com.makeupnow.backend.model.mysql.enums.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponseDTO {
    private Long id;
    private LocalDateTime dateBooking;
    private double totalPrice;
    private BookingStatus status;
    private Long customerId;
    private Long providerId;
    private Long serviceId;
    private Long scheduleId;
}

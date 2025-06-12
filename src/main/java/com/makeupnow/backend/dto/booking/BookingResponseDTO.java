package com.makeupnow.backend.dto.booking;

import com.makeupnow.backend.dto.review.ReviewResponseDTO;
import com.makeupnow.backend.model.mysql.enums.BookingStatus;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class BookingResponseDTO {
    private Long id;
    private LocalDate dateSchedule;     
    private LocalTime timeSchedule;     
    private LocalDateTime dateBooking;
    private double totalPrice;
    private BookingStatus status;
    private Long customerId;
    private Long providerId;
    private Long serviceId;
    private Long scheduleId;

    // Infos client
    private String customerName;     
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;

    // Infos prestataire
    private String providerName;
    private String providerEmail;
    private String providerPhone;
    private String providerAddress;

    // Infos service
    private String serviceTitle;
    private String serviceDuration;  

    // Review associée
    private ReviewResponseDTO review;

    private Long paymentId;

}


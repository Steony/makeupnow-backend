package com.makeupnow.backend.dto.booking;

import com.makeupnow.backend.model.mysql.enums.BookingStatus;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingResponseDTO {
    private Long id;
    private LocalDateTime dateBooking;
    private double totalPrice;
    private BookingStatus status;
    private Long customerId;
    private Long providerId;
    private Long serviceId;
    private Long scheduleId;

   
    private String customerName;     
    private String providerName;     
    private String serviceTitle;    
    private String providerAddress; 
    private String serviceDuration;  
}

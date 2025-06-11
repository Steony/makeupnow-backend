package com.makeupnow.backend.dto.booking;

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

   
    private String customerName;     
    private String providerName;
     private String providerEmail;     
    private String providerPhone;    
    private String serviceTitle;    
    private String providerAddress; 
    private String serviceDuration;  
}

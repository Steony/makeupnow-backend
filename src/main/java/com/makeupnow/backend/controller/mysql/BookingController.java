package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.model.mysql.Booking;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.service.mysql.BookingService;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.exception.InvalidRequestException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired private BookingService bookingService;


    // DTO interne pour la création (tu peux le mettre dans un fichier à part)
    public static class BookingRequest {
        public Long customerId;
        public Long providerId;
        public Long serviceId;
        public Long scheduleId;
        public double totalPrice;
    }

   
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(
            request.customerId,
            request.providerId,
            request.serviceId,
            request.scheduleId,
            request.totalPrice
        );
        return ResponseEntity.status(201).body(booking);
    }

   
   @DeleteMapping("/{id}")
public ResponseEntity<String> deleteBooking(
    @PathVariable Long id,
    @RequestParam Long userId,
    @RequestParam Role userRole) {

    bookingService.deleteBooking(id, userId, userRole);
    return ResponseEntity.ok("Réservation supprimée avec succès.");
}


   
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Booking>> getBookingsByCustomer(@PathVariable Long customerId) {
        List<Booking> bookings = bookingService.getBookingsByCustomer(customerId);
        return ResponseEntity.ok(bookings);
    }

    
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Booking>> getBookingsByProvider(@PathVariable Long providerId) {
        List<Booking> bookings = bookingService.getBookingsByProvider(providerId);
        return ResponseEntity.ok(bookings);
    }

   
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    // Gestion globale des exceptions spécifiques
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<String> handleInvalidRequest(InvalidRequestException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
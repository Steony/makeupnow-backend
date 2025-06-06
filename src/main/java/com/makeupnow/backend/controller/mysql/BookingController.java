package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.dto.booking.BookingCreateDTO;
import com.makeupnow.backend.dto.booking.BookingResponseDTO;
import com.makeupnow.backend.service.mysql.BookingService;

import jakarta.validation.Valid;

import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.exception.InvalidRequestException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    
 @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingCreateDTO request)
 {
        BookingResponseDTO response = bookingService.createBooking(request);
        return ResponseEntity.status(201).body(response);
    }

@PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<String> deleteBooking(@PathVariable Long id) {
    bookingService.deleteBooking(id);
    return ResponseEntity.ok("Réservation annulée avec succès.");
}


 @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByCustomer(@PathVariable Long customerId) {
        List<BookingResponseDTO> bookings = bookingService.getBookingsByCustomer(customerId);
        return ResponseEntity.ok(bookings);
    }

     @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByProvider(@PathVariable Long providerId) {
        List<BookingResponseDTO> bookings = bookingService.getBookingsByProvider(providerId);
        return ResponseEntity.ok(bookings);
    }


@PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        List<BookingResponseDTO> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    // 🔴 Gestion des exceptions personnalisées
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<String> handleInvalidRequest(InvalidRequestException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // ✅ Gestion de l'erreur sur créneau déjà réservé (empêche 500)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.badRequest().body("Une erreur est survenue : " + ex.getMessage());
    }
}

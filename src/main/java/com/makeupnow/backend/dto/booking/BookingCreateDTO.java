package com.makeupnow.backend.dto.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingCreateDTO {

    @NotNull(message = "L'identifiant du client est requis")
    private Long customerId;

    @NotNull(message = "L'identifiant du prestataire est requis")
    private Long providerId;

    @NotNull(message = "L'identifiant du service est requis")
    private Long serviceId;

    @NotNull(message = "L'identifiant du créneau est requis")
    private Long scheduleId;

    @Min(value = 0, message = "Le prix total doit être positif")
    private double totalPrice;
}

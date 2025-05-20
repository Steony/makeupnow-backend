package com.makeupnow.backend.model.mongo;

import lombok.*;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Document(collection = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Review {

    @Id
    private String id;

    @NotNull(message = "L'identifiant du client est obligatoire.")
    private Long customerId;

    private String customerName;

    @NotNull(message = "L'identifiant du prestataire est obligatoire.")
    private Long providerId;

    private String providerName; 

    @NotNull(message = "L'identifiant de la réservation est obligatoire.")
    private String bookingId;

    @NotNull(message = "La note est obligatoire.")
    @Min(value = 1, message = "La note doit être au minimum de 1.")
    @Max(value = 5, message = "La note doit être au maximum de 5.")
    private int rating; // Note de 1 à 5

    @NotNull(message = "Le commentaire est obligatoire.")
    @Size(max = 500, message = "Le commentaire ne doit pas dépasser 500 caractères.")
    private String comment;

    @NotNull(message = "La date du commentaire est obligatoire.")
    private LocalDateTime dateComment;
}

package com.makeupnow.backend.dto.review;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class ReviewCreateDTO {

    @NotNull(message = "L'identifiant du client est obligatoire.")
    private Long customerId;

    @NotNull(message = "L'identifiant du prestataire est obligatoire.")
    private Long providerId;

     @NotNull(message = "L'identifiant de la prestation est obligatoire.")
private Long makeupServiceId;


    @Min(value = 1, message = "La note minimale est 1.")
    @Max(value = 5, message = "La note maximale est 5.")
    private int rating;

    @NotBlank(message = "Le commentaire est obligatoire.")
    @Size(max = 500, message = "Le commentaire ne doit pas dépasser 500 caractères.")
    private String comment;
}

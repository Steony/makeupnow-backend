package com.makeupnow.backend.dto.makeupservice;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MakeupServiceUpdateDTO {

    @NotBlank(message = "Le titre est requis.")
    private String title;

    @NotBlank(message = "La description est requise.")
    private String description;

    @Positive(message = "La durée doit être positive.")
    private int duration;

    @Positive(message = "Le prix doit être positif.")
    private double price;

    @NotNull(message = "L'identifiant du prestataire est requis.")
    private Long providerId;

    @NotNull(message = "L'identifiant de la catégorie est requis.")
    private Long categoryId;
}

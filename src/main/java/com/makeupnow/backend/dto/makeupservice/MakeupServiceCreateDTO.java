package com.makeupnow.backend.dto.makeupservice;

import jakarta.validation.constraints.*;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class MakeupServiceCreateDTO {

    @NotBlank(message = "Le titre est requis.")
    private String title;

    @NotBlank(message = "La description est requise.")
    private String description;

    @Positive(message = "La durée doit être positive.")
    private int duration;

    @Positive(message = "Le prix doit être positif.")
    private double price;

    @NotNull(message = "L'identifiant du prestataire est requis.")
    @JsonProperty("providerId")
    private Long providerId;

    @NotNull(message = "L'identifiant de la catégorie est requis.")
    @JsonProperty("categoryId")
    private Long categoryId;
}

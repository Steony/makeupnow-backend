package com.makeupnow.backend.dto.makeupservice;

import lombok.Data;

@Data
public class MakeupServiceResponseDTO {
    private Long id;
    private String title;
    private String description;
    private int duration;
    private double price;
    private Long categoryId;
    private String categoryTitle;
    private Long providerId;
    private String providerName;
}

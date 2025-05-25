package com.makeupnow.backend.dto.makeupservice;

import lombok.Data;

@Data
public class MakeupServiceUpdateDTO {
    private String title;
    private String description;
    private int duration;
    private double price;
    private Long providerId;
    private Long categoryId;
}

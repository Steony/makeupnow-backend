package com.makeupnow.backend.dto.review;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponseDTO {

    private String id;

    private Long customerId;
    private String customerName;

    private Long providerId;
    private String providerName;

    private Long makeupServiceId;

    private Long bookingId; 

    private int rating;
    private String comment;
    private LocalDateTime dateComment;
}

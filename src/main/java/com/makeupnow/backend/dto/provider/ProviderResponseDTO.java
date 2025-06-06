package com.makeupnow.backend.dto.provider;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderResponseDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String address;
    private Double averageRating;
}

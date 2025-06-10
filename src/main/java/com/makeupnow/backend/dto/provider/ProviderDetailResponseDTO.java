package com.makeupnow.backend.dto.provider;

import java.util.List;

import com.makeupnow.backend.dto.makeupservice.MakeupServiceResponseDTO;
import com.makeupnow.backend.dto.review.ReviewResponseDTO;
import com.makeupnow.backend.dto.schedule.ScheduleResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderDetailResponseDTO {
private Long id;
    private String firstname;
    private String lastname;
    private String address;
    private Double averageRating;
    private String categoriesString; // exemple: "Beauté, SFX, Mariage"

    // Listes détaillées (déjà mappées)
    private List<MakeupServiceResponseDTO> services;
    private List<ScheduleResponseDTO> schedules;
    private List<ReviewResponseDTO> reviews;
}

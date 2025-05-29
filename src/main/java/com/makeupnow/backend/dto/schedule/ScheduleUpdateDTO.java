package com.makeupnow.backend.dto.schedule;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ScheduleUpdateDTO {

    @NotNull(message = "La date de début est requise.")
    private LocalDateTime startTime;

    @NotNull(message = "La date de fin est requise.")
    private LocalDateTime endTime;
}
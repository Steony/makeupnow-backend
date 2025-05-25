package com.makeupnow.backend.dto.schedule;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleResponseDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long providerId;
}

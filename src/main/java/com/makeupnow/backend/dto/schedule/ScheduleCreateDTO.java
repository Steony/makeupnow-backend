package com.makeupnow.backend.dto.schedule;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleCreateDTO {
    private Long providerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

package com.makeupnow.backend.dto.useractionlog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActionLogResponseDTO {
    private Long id;

    // Affiche soit le prénom + nom, soit "Anonyme"
    private String user; 

    private String action;
    private String description;
    private LocalDateTime timestamp;
    private boolean anonymized;
}

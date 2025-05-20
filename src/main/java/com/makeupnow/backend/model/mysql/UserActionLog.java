package com.makeupnow.backend.model.mysql;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "user_action_log")
public class UserActionLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation avec User (nullable pour anonymisation)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @NotNull
    @Size(min = 3, max = 100, message = "L'action doit comporter entre 3 et 100 caractères.")
    @Column(nullable = false)
    private String action;

    @NotNull
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Builder.Default
    @Column(name = "anonymized", nullable = false)
    private boolean anonymized = false;
}

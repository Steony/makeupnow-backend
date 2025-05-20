package com.makeupnow.backend.model.mysql;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime; // ex : 2025-03-23T09:00

    @Column(nullable = false)
    private LocalDateTime endTime;   // ex : 2025-03-23T12:00
    
    // Relation avec Provider (prestataire)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    // Relation avec Booking (Réservation)
    @JsonIgnore
    @OneToOne(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Booking booking;
}

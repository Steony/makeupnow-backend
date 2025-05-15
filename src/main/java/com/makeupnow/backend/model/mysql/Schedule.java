package com.makeupnow.backend.model.mysql;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime; // ex : 2025-03-23T09:00
    private LocalDateTime endTime;   // ex : 2025-03-23T12:00
    

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @OneToOne(mappedBy = "schedule")
    private Booking booking;

}

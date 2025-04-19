package com.makeupnow.backend.model.mysql;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private int duration; // en minutes par exemple
    private double price;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;
}

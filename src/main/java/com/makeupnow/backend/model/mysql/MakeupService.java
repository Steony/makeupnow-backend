package com.makeupnow.backend.model.mysql;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.validation.constraints.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "service")
public class MakeupService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String title;

    @NotNull
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Positive
    @Column(nullable = false)
    private int duration; // en minutes

    @Positive
    @Column(nullable = false)
    private double price;

    // Relation avec Provider (Prestataire)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    // Relation avec Category (Catégorie)
   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}

package com.makeupnow.backend.model.mysql;

import java.time.LocalDateTime;
import com.makeupnow.backend.model.mysql.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateBooking;

    @NotNull
    private double totalPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    // Relation avec Customer (Client)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Relation avec Provider (Prestataire)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    // Relation avec MakeupService
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private MakeupService service;

    // Relation avec Schedule (Créneau)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "schedule_id", nullable = false)
private Schedule schedule;


    // Relation avec Payment (Paiement)
    @JsonIgnore
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;
}

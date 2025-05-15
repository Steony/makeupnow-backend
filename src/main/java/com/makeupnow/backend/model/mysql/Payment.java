package com.makeupnow.backend.model.mysql;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String method; // "CB", "Espèces", "PayPal"
    private double amount;
    private String status; // "PENDING", "PAID", "REFUNDED"

    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = true)
    private Provider provider;
}

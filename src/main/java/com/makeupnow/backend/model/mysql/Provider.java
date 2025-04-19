package com.makeupnow.backend.model.mysql;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Provider extends User {

    @Enumerated(EnumType.STRING)
    private CertificationStatus certificationStatus;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<Service> services;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<Schedule> availability;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<Payment> payments;
}

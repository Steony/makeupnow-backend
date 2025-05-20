package com.makeupnow.backend.model.mysql;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.makeupnow.backend.model.mysql.enums.Role;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "provider")
@DiscriminatorValue("PROVIDER")
public class Provider extends User {

    private boolean isCertified;

    // Association avec les services proposés par le Provider
    @JsonIgnore
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MakeupService> services;

    // Association avec les créneaux de disponibilité
    @JsonIgnore
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Schedule> availability;

    // Association avec les réservations (Booking)
    @JsonIgnore
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    // Association avec les paiements liés aux réservations
    @JsonIgnore
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Payment> payments;

    // Constructeur pour créer un Provider avec les attributs hérités de User
    public Provider(Long id, String firstname, String lastname, String email, 
                    String password, String address, String phoneNumber, 
                    Role role, boolean isActive, boolean isCertified) {
        super(id, firstname, lastname, email, password, address, phoneNumber, role, isActive);
        this.isCertified = isCertified;
    }

      // Constructeur simplifié avec juste l'ID
    public Provider(Long id) {
        this.setId(id);
    }
}

package com.makeupnow.backend.model.mysql;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "customer")
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {

    // Relation avec les réservations (Booking)
    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Booking> bookings;


    // Constructeur pour créer un Customer avec les attributs hérités de User
    public Customer(Long id, String firstname, String lastname, String email, 
                    String password, String address, String phoneNumber, 
                    Role role, boolean isActive) {
        super(id, firstname, lastname, email, password, address, phoneNumber, role, isActive);
    }
}

package com.makeupnow.backend.model.mysql;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.makeupnow.backend.model.mysql.enums.CertificationStatus;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Provider extends User {

    @Enumerated(EnumType.STRING)
    private CertificationStatus certificationStatus;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
     @JsonIgnore
    private List<Service> services;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
     @JsonIgnore
    private List<Schedule> availability;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
     @JsonIgnore
    private List<Booking> bookings;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
     @JsonIgnore
    private List<Payment> payments;
}

/* À retenir :
@JsonIgnore empêche la sérialisation JSON d'une relation.

Tu le mets toujours sur le côté où la relation est la plus complexe (souvent dans les @OneToMany).

Si un jour tu as besoin d'accéder à ces relations dans une réponse JSON, tu pourras les exposer via un DTO (Data Transfer Object) spécifique.
 */
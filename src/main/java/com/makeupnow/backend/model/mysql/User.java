package com.makeupnow.backend.model.mysql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.makeupnow.backend.model.mysql.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "app_user")
@DynamicUpdate
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;
    private String email;

    @JsonIgnore // Empêche l'affichage du mot de passe dans les réponses JSON
    private String password;

    private String address;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonProperty("isActive")
    private boolean isActive;

    // Méthode pour la sérialisation JSON (isActive)
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}

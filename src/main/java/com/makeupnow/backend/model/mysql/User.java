package com.makeupnow.backend.model.mysql;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.makeupnow.backend.model.mysql.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user") 
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String address;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonProperty("isActive")
    private boolean isActive;

    // ✅ Méthode pour la sérialisation JSON (isActive)
    public boolean isIsActive() {
        return isActive;
    }
    
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}

package com.makeupnow.backend.model.mysql;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.makeupnow.backend.model.mysql.enums.Role;

@Entity
@Getter
@Setter
@NoArgsConstructor  // Ajouté pour JPA
@SuperBuilder
@Table(name = "admin")
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    // Constructeur complet avec tous les attributs hérités
    public Admin(Long id, String firstname, String lastname, String email, 
                 String password, String address, String phoneNumber, 
                 Role role, boolean isActive) {
        super(id, firstname, lastname, email, password, address, phoneNumber, role, isActive);
    }
}

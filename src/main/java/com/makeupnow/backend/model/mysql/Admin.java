package com.makeupnow.backend.model.mysql;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends User {

    // Relation avec les logs d'actions utilisateurs
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserActionLog> userActionLogs;

}

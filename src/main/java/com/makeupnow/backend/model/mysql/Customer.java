package com.makeupnow.backend.model.mysql;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.makeupnow.backend.model.mysql.enums.Role;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
public class Customer extends User {

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Payment> payments;

 // ✅ Constructeur public uniquement pour les tests
public Customer(Long id, String firstname, String lastname, String email, String password,
                   String address, String phoneNumber, Role role, boolean isActive) {
    super(id, firstname, lastname, email, password, address, phoneNumber, role, isActive);
}


}

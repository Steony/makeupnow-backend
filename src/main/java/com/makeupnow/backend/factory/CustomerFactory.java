package com.makeupnow.backend.factory;

import com.makeupnow.backend.model.mysql.Customer;
import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.model.mysql.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomerFactory implements UserFactory {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(String firstname, String lastname, String email, String password) {
        Customer customer = new Customer();
        customer.setFirstname(firstname);
        customer.setLastname(lastname);
        customer.setEmail(email);
        customer.setPassword(passwordEncoder.encode(password));
        customer.setRole(Role.CLIENT);
        customer.setActive(true);
        return customer;
    }
}

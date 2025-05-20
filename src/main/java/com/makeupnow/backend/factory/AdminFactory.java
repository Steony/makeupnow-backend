package com.makeupnow.backend.factory;

import com.makeupnow.backend.model.mysql.Admin;
import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.model.mysql.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminFactory implements UserFactory {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(String firstname, String lastname, String email, String password) {
        Admin admin = new Admin();
        admin.setFirstname(firstname);
        admin.setLastname(lastname);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(Role.ADMIN);
        admin.setActive(true);
        return admin;
    }
}

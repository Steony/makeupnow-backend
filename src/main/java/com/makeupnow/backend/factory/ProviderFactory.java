package com.makeupnow.backend.factory;

import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.model.mysql.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ProviderFactory implements UserFactory {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ProviderFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(String firstname, String lastname, String email, String password) {
        Provider provider = new Provider();
        provider.setFirstname(firstname);
        provider.setLastname(lastname);
        provider.setEmail(email);
        provider.setPassword(passwordEncoder.encode(password));
        provider.setRole(Role.PROVIDER);
        provider.setActive(true);
        return provider;
    }
}

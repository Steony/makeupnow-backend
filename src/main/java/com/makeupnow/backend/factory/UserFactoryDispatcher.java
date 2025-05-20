package com.makeupnow.backend.factory;

import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.model.mysql.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserFactoryDispatcher {

    private final Map<Role, UserFactory> factoryMap = new HashMap<>();

    @Autowired
    public UserFactoryDispatcher(AdminFactory adminFactory, CustomerFactory customerFactory, ProviderFactory providerFactory) {
        factoryMap.put(Role.ADMIN, adminFactory);
        factoryMap.put(Role.CLIENT, customerFactory);
        factoryMap.put(Role.PROVIDER, providerFactory);
    }

    // Méthode de création centralisée
    public User createUser(Role role, String firstname, String lastname, String email, String password) {
        UserFactory factory = factoryMap.get(role);
        if (factory == null) {
            throw new IllegalArgumentException("Factory non définie pour le rôle : " + role);
        }
        return factory.createUser(firstname, lastname, email, password);
    }
}

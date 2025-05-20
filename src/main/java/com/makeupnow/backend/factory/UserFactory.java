package com.makeupnow.backend.factory;

import com.makeupnow.backend.model.mysql.User;

public interface UserFactory {
    User createUser(String firstname, String lastname, String email, String password);
}

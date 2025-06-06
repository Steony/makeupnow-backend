package com.makeupnow.backend.model.mysql.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    CLIENT,
    PROVIDER,
    ADMIN;

    @JsonCreator
    public static Role fromString(String key) {
        if (key == null) {
            return null;
        }
        return Role.valueOf(key.toUpperCase());
    }
}

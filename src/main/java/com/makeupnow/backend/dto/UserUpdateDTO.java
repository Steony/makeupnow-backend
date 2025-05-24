package com.makeupnow.backend.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String address;
    private String phoneNumber;
}

package com.makeupnow.backend.dto;

import com.makeupnow.backend.model.mysql.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String address;
    private String phoneNumber;
    private Role role;
}

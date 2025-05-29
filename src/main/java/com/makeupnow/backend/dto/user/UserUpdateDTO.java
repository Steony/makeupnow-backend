package com.makeupnow.backend.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserUpdateDTO {

    private Long id;

    @Size(min = 2, message = "Le prénom doit contenir au moins 2 caractères")
    private String firstname;

    @Size(min = 2, message = "Le nom doit contenir au moins 2 caractères")
    private String lastname;

    @Email(message = "Email invalide")
    private String email;

    @Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
    message = "Le mot de passe doit contenir au moins 8 caractères, avec au moins une majuscule, une minuscule, un chiffre et un caractère spécial."
    ) private String password;

    private String address;

    private String phoneNumber;
}

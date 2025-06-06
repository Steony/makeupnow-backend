package com.makeupnow.backend.dto.user;

import com.makeupnow.backend.model.mysql.enums.Role;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {

    @NotBlank(message = "Le prénom est requis")
    private String firstname;

    @NotBlank(message = "Le nom est requis")
    private String lastname;

    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est requis")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
      message = "Le mot de passe doit contenir au moins 8 caractères, " +
                "avec au moins une majuscule, une minuscule, un chiffre et un caractère spécial."
    )
    private String password;

    @NotBlank(message = "L'adresse est requise")
    private String address;
@NotBlank(message = "Le numéro de téléphone est requis")
    private String phoneNumber;

    @NotNull(message = "Le rôle est requis")
    private Role role;

}

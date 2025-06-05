package com.makeupnow.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateDTO {
    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;
}

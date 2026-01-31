package com.fleetmanagement.fleetsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.security.core.parameters.P;

@Data
public class LoginRequest {

    @NotBlank(message="Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message="Password is required")
    private String password;
}

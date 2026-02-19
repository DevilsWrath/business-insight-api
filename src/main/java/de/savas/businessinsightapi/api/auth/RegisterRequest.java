package de.savas.businessinsightapi.api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @Email String email,
        @NotBlank String password
) {}
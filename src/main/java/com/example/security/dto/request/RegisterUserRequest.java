package com.example.security.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotEmpty(message = "Nome é obrigatório") String name,
        @NotEmpty(message = "Email é obrigatório") @Email(message = "Email inválido") String email,
        @NotEmpty(message = "Senha é obrigatória") @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres") String password
) {}


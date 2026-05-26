package com.example.security.dto.request;

import com.example.security.entity.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeUserRoleRequest {

    @NotNull(message = "Role é obrigatória")
    private UserRole role;
}

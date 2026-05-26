package com.example.security.dto.response;

import com.example.security.entity.User;

public record AdminUserResponse(Long id, String name, String email, String role) {

    public static AdminUserResponse from(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}

package com.example.security.dto.response;

import com.example.security.entity.User;

public record UserProfileResponse(Long id, String name, String email, String role) {

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}

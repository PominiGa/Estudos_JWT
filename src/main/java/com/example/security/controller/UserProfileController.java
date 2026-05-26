package com.example.security.controller;

import com.example.security.dto.ApiResponse;
import com.example.security.dto.request.UpdateProfileRequest;
import com.example.security.dto.response.UserProfileResponse;
import com.example.security.entity.User;
import com.example.security.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(@AuthenticationPrincipal User user) {
        return ApiResponse.ok("Perfil", userProfileService.getProfile(user));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.ok("Perfil atualizado", userProfileService.updateProfile(user, request));
    }
}

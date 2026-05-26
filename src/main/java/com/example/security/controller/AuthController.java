package com.example.security.controller;

import com.example.security.dto.ApiResponse;
import com.example.security.dto.ChangePasswordDTO;
import com.example.security.dto.DeleteUserDTO;
import com.example.security.dto.request.LoginRequest;
import com.example.security.dto.request.RegisterUserRequest;
import com.example.security.dto.response.LoginResponse;
import com.example.security.dto.response.RegisterUserResponse;
import com.example.security.entity.User;
import com.example.security.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Login realizado com sucesso", authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterUserResponse>> register(@Valid @RequestBody RegisterUserRequest request) {
        return ApiResponse.created("Usuário cadastrado com sucesso", authService.register(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordDTO dto) {
        authService.changePassword(user.getEmail(), dto);
        return ApiResponse.noContent("Senha alterada com sucesso");
    }

    @PutMapping("/become-seller")
    public ResponseEntity<ApiResponse<Void>> becomeSeller(
            @AuthenticationPrincipal User user,
            @RequestParam("document") String document) {
        authService.changeUserForSeller(user.getEmail(), document);
        return ApiResponse.noContent("Conta convertida para seller com sucesso");
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DeleteUserDTO dto) {
        authService.deleteUser(user.getEmail(), dto);
        return ApiResponse.noContent("Conta removida com sucesso");
    }
}

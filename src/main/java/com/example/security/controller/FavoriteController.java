package com.example.security.controller;

import com.example.security.dto.ApiResponse;
import com.example.security.dto.response.FavoriteResponse;
import com.example.security.entity.User;
import com.example.security.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FavoriteResponse>>> getMyFavorites(@AuthenticationPrincipal User user) {
        return ApiResponse.ok("Seus favoritos", favoriteService.getMyFavorites(user));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<FavoriteResponse>> add(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {
        return ApiResponse.created("Adicionado aos favoritos", favoriteService.addFavorite(user, productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> remove(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {
        favoriteService.removeFavorite(user, productId);
        return ApiResponse.noContent("Removido dos favoritos");
    }
}

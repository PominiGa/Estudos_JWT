package com.example.security.controller;

import com.example.security.dto.ApiResponse;
import com.example.security.dto.request.CartItemRequest;
import com.example.security.dto.response.CartResponse;
import com.example.security.entity.User;
import com.example.security.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@AuthenticationPrincipal User user) {
        return ApiResponse.ok("Carrinho", cartService.getCart(user));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CartItemRequest request) {
        return ApiResponse.ok("Item adicionado ao carrinho", cartService.addItem(user, request));
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId,
            @RequestParam @Min(0) int quantity) {
        return ApiResponse.ok("Quantidade atualizada", cartService.updateItemQuantity(user, itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId) {
        return ApiResponse.ok("Item removido", cartService.removeItem(user, itemId));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ApiResponse.noContent("Carrinho esvaziado");
    }
}

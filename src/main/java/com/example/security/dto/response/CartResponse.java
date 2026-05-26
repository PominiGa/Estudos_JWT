package com.example.security.dto.response;

import com.example.security.entity.Cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        List<CartItemResponse> items,
        int itemCount,
        BigDecimal total
) {
    public static CartResponse from(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(CartItemResponse::from)
                .toList();
        return new CartResponse(
                cart.getId(),
                items,
                cart.getItemCount(),
                cart.getTotal()
        );
    }
}
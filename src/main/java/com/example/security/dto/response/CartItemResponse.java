package com.example.security.dto.response;

import com.example.security.entity.CartItem;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        String productImageUrl,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal subtotal
) {
    public static CartItemResponse from(CartItem item) {
        BigDecimal unitPrice = item.getProduct().getPrice();
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getImageUrl(),
                unitPrice,
                item.getQuantity(),
                unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()))
        );
    }
}
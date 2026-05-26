package com.example.security.dto.response;

import com.example.security.entity.Products;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String category,
        String subcategory,
        String ean,
        BigDecimal price,
        Integer stockQuantity,
        String imageUrl,
        boolean active,
        Long ownerId,
        String ownerName
) {
    public static ProductResponse from(Products p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getCategory(),
                p.getSubcategory(),
                p.getEAN(),
                p.getPrice(),
                p.getStockQuantity(),
                p.getImageUrl(),
                p.isActive(),
                p.getOwner() != null ? p.getOwner().getId() : null,
                p.getOwner() != null ? p.getOwner().getName() : null
        );
    }
}

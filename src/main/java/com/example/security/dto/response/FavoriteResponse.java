package com.example.security.dto.response;

import com.example.security.entity.Favorite;

import java.time.LocalDateTime;

public record FavoriteResponse(Long id, ProductResponse product, LocalDateTime addedAt) {

    public static FavoriteResponse from(Favorite favorite) {
        return new FavoriteResponse(
                favorite.getId(),
                ProductResponse.from(favorite.getProduct()),
                favorite.getCreatedAt()
        );
    }
}

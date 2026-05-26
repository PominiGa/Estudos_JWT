package com.example.security.service;

import com.example.security.dto.response.FavoriteResponse;
import com.example.security.entity.Favorite;
import com.example.security.entity.Products;
import com.example.security.entity.User;
import com.example.security.exception.FavoriteNotFoundException;
import com.example.security.exception.ProductNotFoundException;
import com.example.security.repository.FavoriteRepository;
import com.example.security.repository.ProductsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductsRepository productsRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, ProductsRepository productsRepository) {
        this.favoriteRepository = favoriteRepository;
        this.productsRepository = productsRepository;
    }

    public List<FavoriteResponse> getMyFavorites(User user) {
        return favoriteRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(FavoriteResponse::from)
                .toList();
    }

    @Transactional
    public FavoriteResponse addFavorite(User user, Long productId) {
        Products product = productsRepository.findById(productId)
                .filter(Products::isActive)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado"));

        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            throw new IllegalStateException("Produto já está nos favoritos");
        }

        Favorite saved = favoriteRepository.save(new Favorite(user, product));
        return FavoriteResponse.from(saved);
    }

    @Transactional
    public void removeFavorite(User user, Long productId) {
        Products product = productsRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado"));

        if (!favoriteRepository.existsByUserAndProduct(user, product)) {
            throw new FavoriteNotFoundException();
        }

        favoriteRepository.deleteByUserAndProduct(user, product);
    }
}

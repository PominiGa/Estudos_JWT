package com.example.security.repository;

import com.example.security.entity.Favorite;
import com.example.security.entity.Products;
import com.example.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserOrderByCreatedAtDesc(User user);
    Optional<Favorite> findByUserAndProduct(User user, Products product);
    boolean existsByUserAndProduct(User user, Products product);
    void deleteByUserAndProduct(User user, Products product);
}

package com.example.security.repository;

import com.example.security.entity.Cart;
import com.example.security.entity.CartItem;
import com.example.security.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Products product);
}

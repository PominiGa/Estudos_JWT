package com.example.security.service;

import com.example.security.dto.request.CartItemRequest;
import com.example.security.dto.response.CartResponse;
import com.example.security.entity.Cart;
import com.example.security.entity.CartItem;
import com.example.security.entity.Products;
import com.example.security.entity.User;
import com.example.security.exception.CartNotFoundException;
import com.example.security.exception.InsufficientStockException;
import com.example.security.exception.ProductNotFoundException;
import com.example.security.repository.CartItemRepository;
import com.example.security.repository.CartRepository;
import com.example.security.repository.ProductsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductsRepository productsRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductsRepository productsRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productsRepository = productsRepository;
    }

    public CartResponse getCart(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(new Cart(user)));
        return CartResponse.from(cart);
    }

    @Transactional
    public CartResponse addItem(User user, CartItemRequest request) {
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(new Cart(user)));

        Products product = productsRepository.findById(request.getProductId())
                .filter(Products::isActive)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado"));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(product.getName(), product.getStockQuantity());
        }

        cartItemRepository.findByCartAndProduct(cart, product).ifPresentOrElse(
                existing -> existing.setQuantity(existing.getQuantity() + request.getQuantity()),
                () -> cart.getItems().add(new CartItem(cart, product, request.getQuantity()))
        );

        Cart saved = cartRepository.save(cart);
        return CartResponse.from(saved);
    }

    @Transactional
    public CartResponse updateItemQuantity(User user, Long itemId, int quantity) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(CartNotFoundException::new);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(CartNotFoundException::new);

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            if (item.getProduct().getStockQuantity() < quantity) {
                throw new InsufficientStockException(item.getProduct().getName(), item.getProduct().getStockQuantity());
            }
            item.setQuantity(quantity);
        }

        Cart saved = cartRepository.save(cart);
        return CartResponse.from(saved);
    }

    @Transactional
    public CartResponse removeItem(User user, Long itemId) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(CartNotFoundException::new);

        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        Cart saved = cartRepository.save(cart);
        return CartResponse.from(saved);
    }

    @Transactional
    public void clearCart(User user) {
        cartRepository.findByUser(user).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }
}
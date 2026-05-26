package com.example.security.exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException() {
        super("Carrinho não encontrado");
    }
}
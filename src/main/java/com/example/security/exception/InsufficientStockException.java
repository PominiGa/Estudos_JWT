package com.example.security.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productName, int available) {
        super(String.format("Estoque insuficiente para '%s'. Disponível: %d", productName, available));
    }
}
package com.example.security.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("Pedido não encontrado");
    }
    public OrderNotFoundException(String message) {
        super(message);
    }
}

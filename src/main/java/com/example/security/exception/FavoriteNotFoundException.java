package com.example.security.exception;

public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException() {
        super("Favorito não encontrado");
    }
}
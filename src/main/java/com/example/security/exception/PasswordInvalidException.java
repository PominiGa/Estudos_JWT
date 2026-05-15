package com.example.security.exception;

public class PasswordInvalidException extends RuntimeException {

    public PasswordInvalidException() {
        super("Senha invalida");
    }
}

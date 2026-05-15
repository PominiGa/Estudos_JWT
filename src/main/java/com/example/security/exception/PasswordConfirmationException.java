package com.example.security.exception;

public class PasswordConfirmationException extends RuntimeException {

    public PasswordConfirmationException() {
        super("A confirmação da senha não confere");
    }
}
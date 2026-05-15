package com.example.security.exception;

public class SamePasswordException extends RuntimeException {

    public SamePasswordException() {
        super("A nova senha não pode ser igual à senha atual");
    }
}
package com.example.security.exception;

public class ProductForbiddenException extends RuntimeException {
    public ProductForbiddenException() { super(); }
    public ProductForbiddenException(String message) { super(message); }
}
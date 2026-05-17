package com.example.security.exception;

public class DocumentInvalidException extends RuntimeException {
    public DocumentInvalidException() { super(); }
    public DocumentInvalidException(String message) { super(message); }
    public DocumentInvalidException(String message, Throwable cause) { super(message, cause); }
}
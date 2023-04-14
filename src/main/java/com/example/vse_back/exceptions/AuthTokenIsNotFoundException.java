package com.example.vse_back.exceptions;

public class AuthTokenIsNotFoundException extends RuntimeException {
    public AuthTokenIsNotFoundException(String token) {
        super(String.format("Authentication token %s was not found", token));
    }
}

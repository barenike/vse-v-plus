package com.example.vse_back.exceptions;

public class TokenIsNotFoundException extends RuntimeException {
    public TokenIsNotFoundException(String message, String token) {
        super(String.format("%s %s does not exist.", message, token));
    }
}

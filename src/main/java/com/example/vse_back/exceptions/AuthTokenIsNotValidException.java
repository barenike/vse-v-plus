package com.example.vse_back.exceptions;

public class AuthTokenIsNotValidException extends RuntimeException {
    public AuthTokenIsNotValidException(String token) {
        super(String.format("Authentication token %s is not valid", token));
    }
}

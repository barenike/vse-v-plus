package com.example.vse_back.exceptions;

public class AuthTokenHasExpiredException extends RuntimeException {
    public AuthTokenHasExpiredException(String token) {
        super(String.format("Authentication token %s has expired", token));
    }
}

package com.example.vse_back.exceptions;

public class AuthCodeHasExpiredException extends RuntimeException {
    public AuthCodeHasExpiredException(String code) {
        super(String.format("Authentication code %s has expired", code));
    }
}

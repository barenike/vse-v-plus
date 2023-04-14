package com.example.vse_back.exceptions;

public class AuthCodeIsNotFoundException extends RuntimeException {
    public AuthCodeIsNotFoundException(String email) {
        super(String.format("Authentication code with email %s was not found", email));
    }
}

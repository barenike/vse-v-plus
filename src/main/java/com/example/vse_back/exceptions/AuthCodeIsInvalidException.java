package com.example.vse_back.exceptions;

public class AuthCodeIsInvalidException extends RuntimeException {
    public AuthCodeIsInvalidException(String code) {
        super(String.format("Authentication code %s is invalid", code));
    }
}

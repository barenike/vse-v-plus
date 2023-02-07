package com.example.vse_back.exceptions;

public class UserIsNotFoundException extends RuntimeException {
    public UserIsNotFoundException(String userId) {
        super(String.format("User is not found by %s userId.", userId));
    }
}

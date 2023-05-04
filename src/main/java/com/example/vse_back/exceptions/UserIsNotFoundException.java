package com.example.vse_back.exceptions;

public class UserIsNotFoundException extends RuntimeException {
    public UserIsNotFoundException(String email) {
        super(String.format("There is no user with the email %s", email));
    }
}

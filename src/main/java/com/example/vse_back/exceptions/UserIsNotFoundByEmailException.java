package com.example.vse_back.exceptions;

public class UserIsNotFoundByEmailException extends RuntimeException {
    public UserIsNotFoundByEmailException() {
        super("There is no account with this email.");
    }
}

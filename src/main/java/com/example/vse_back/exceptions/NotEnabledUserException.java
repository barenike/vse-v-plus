package com.example.vse_back.exceptions;

public class NotEnabledUserException extends RuntimeException {
    public NotEnabledUserException() {
        super("The account is not enabled.");
    }
}

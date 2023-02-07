package com.example.vse_back.exceptions;

public class IncorrectEmailException extends RuntimeException {
    public IncorrectEmailException(String email) {
        super(String.format("There is no account with %s email.", email));
    }
}

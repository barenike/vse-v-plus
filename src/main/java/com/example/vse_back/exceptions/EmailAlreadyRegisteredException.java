package com.example.vse_back.exceptions;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(String email) {
        super(String.format("The account with the %s email already exists.", email));
    }
}

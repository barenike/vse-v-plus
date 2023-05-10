package com.example.vse_back.exceptions;

public class UserIsDisabledException extends RuntimeException {
    public UserIsDisabledException() {
        super("User is disabled");
    }
}

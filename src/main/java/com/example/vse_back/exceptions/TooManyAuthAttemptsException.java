package com.example.vse_back.exceptions;

public class TooManyAuthAttemptsException extends RuntimeException {
    public TooManyAuthAttemptsException() {
        super("Too many unsuccessful authentication attempts");
    }
}

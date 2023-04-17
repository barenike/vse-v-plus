package com.example.vse_back.exceptions;

public class InvalidOrderStatusException extends RuntimeException {
    public InvalidOrderStatusException() {
        super("You have submitted CREATED status which is not allowed since the creation date should not be changed");
    }
}

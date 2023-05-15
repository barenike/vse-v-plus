package com.example.vse_back.exceptions;

public class TransferToItselfException extends RuntimeException {
    public TransferToItselfException() {
        super("You cannot transfer coins to yourself");
    }
}

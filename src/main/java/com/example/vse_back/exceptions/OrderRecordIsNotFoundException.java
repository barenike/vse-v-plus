package com.example.vse_back.exceptions;

public class OrderRecordIsNotFoundException extends RuntimeException {
    public OrderRecordIsNotFoundException() {
        super("Order record with this UUID does not exist.");
    }
}

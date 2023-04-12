package com.example.vse_back.exceptions;

import java.util.UUID;

public class OrderRecordIsNotFoundException extends RuntimeException {
    public OrderRecordIsNotFoundException(UUID orderRecordId) {
        super(String.format("Order record with %s UUID does not exist.", orderRecordId));
    }
}

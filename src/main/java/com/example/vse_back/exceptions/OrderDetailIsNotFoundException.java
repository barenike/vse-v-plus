package com.example.vse_back.exceptions;

import java.util.UUID;

public class OrderDetailIsNotFoundException extends RuntimeException {
    public OrderDetailIsNotFoundException(UUID orderDetailId) {
        super(String.format("Order detail with %s UUID does not exist", orderDetailId));
    }
}

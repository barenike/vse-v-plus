package com.example.vse_back.exceptions;

import java.util.UUID;

public class NotEnoughProductException extends RuntimeException {
    public NotEnoughProductException(UUID productId, Integer wantedAmount, Integer realAmount) {
        super(String.format("You want %s of product with id %s when there is/are only %s of it",
                productId, wantedAmount, realAmount));
    }
}

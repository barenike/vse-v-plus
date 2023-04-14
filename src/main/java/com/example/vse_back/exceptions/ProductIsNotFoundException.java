package com.example.vse_back.exceptions;

public class ProductIsNotFoundException extends RuntimeException {
    public ProductIsNotFoundException(String productId) {
        super(String.format("Product with %s UUID does not exist", productId));
    }
}

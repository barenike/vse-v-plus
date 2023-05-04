package com.example.vse_back.exceptions;

public class EntityIsNotFoundException extends RuntimeException {
    public EntityIsNotFoundException(String entityName, String id) {
        super(String.format("There is no %s with the id %s", entityName, id));
    }
}

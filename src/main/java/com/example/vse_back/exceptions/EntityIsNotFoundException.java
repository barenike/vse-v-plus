package com.example.vse_back.exceptions;

import java.util.UUID;

public class EntityIsNotFoundException extends RuntimeException {
    public EntityIsNotFoundException(String entityName, UUID id) {
        super(String.format("There is no %s with the id %s", entityName, id));
    }
}

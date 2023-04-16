package com.example.vse_back.exceptions;

public class PostIsNotFoundException extends RuntimeException {
    public PostIsNotFoundException(String postId) {
        super(String.format("Post with %s UUID does not exist", postId));
    }
}
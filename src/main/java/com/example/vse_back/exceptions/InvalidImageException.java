package com.example.vse_back.exceptions;

public class InvalidImageException extends RuntimeException {
    public InvalidImageException() {
        super("Submitted image is invalid");
    }
}

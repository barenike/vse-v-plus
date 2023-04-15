package com.example.vse_back.exceptions;

public class InputFileIsNotImageException extends RuntimeException {
    public InputFileIsNotImageException() {
        super("The file is not an image");
    }
}

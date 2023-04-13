package com.example.vse_back.exceptions;

public class ImageDeleteFromDropboxFailedException extends RuntimeException {
    public ImageDeleteFromDropboxFailedException(String filePath, Throwable cause) {
        super(String.format("Failed to delete image from Dropbox with file path: %s", filePath), cause);
    }
}

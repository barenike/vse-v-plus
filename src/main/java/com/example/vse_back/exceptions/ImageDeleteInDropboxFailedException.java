package com.example.vse_back.exceptions;

public class ImageDeleteInDropboxFailedException extends RuntimeException {
    public ImageDeleteInDropboxFailedException(String filePath, Throwable cause) {
        super(String.format("Failed to delete image in Dropbox with file path: %s", filePath), cause);
    }
}

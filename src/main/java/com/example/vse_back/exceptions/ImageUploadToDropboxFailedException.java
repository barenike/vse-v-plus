package com.example.vse_back.exceptions;

public class ImageUploadToDropboxFailedException extends RuntimeException {
    public ImageUploadToDropboxFailedException(String filePath, Throwable cause) {
        super(String.format("Image upload to Dropbox with path %s has failed.", filePath), cause);
    }
}

package com.example.vse_back.exceptions.exception_handler;

import com.example.vse_back.exceptions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ImageDeleteFromDropboxFailedException.class, ImageUploadToDropboxFailedException.class})
    public ResponseEntity<Object> handleInternalServerErrorException(ImageDeleteFromDropboxFailedException e, WebRequest request) {
        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({
            AuthCodeHasExpiredException.class,
            AuthCodeIsInvalidException.class,
            AuthCodeIsNotFoundException.class,
            InputFileIsNotImageException.class,
            NotEnabledUserException.class,
            NotEnoughCoinsException.class,
            OrderDetailIsNotFoundException.class,
            ProductIsNotFoundException.class,
            TooManyAuthAttemptsException.class,
            UserIsNotFoundException.class
    })
    public ResponseEntity<Object> handleForbiddenException(Exception e, WebRequest request) {
        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    public ResponseEntity<Object> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}

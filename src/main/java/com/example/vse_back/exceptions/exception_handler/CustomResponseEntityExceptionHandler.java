package com.example.vse_back.exceptions.exception_handler;

import com.example.vse_back.exceptions.*;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Log
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
            EntityIsNotFoundException.class,
            InvalidImageException.class,
            NotEnabledUserException.class,
            NotEnoughCoinsException.class,
            TooManyAuthAttemptsException.class,
            UserIsDisabledException.class,
            UserIsNotFoundException.class
    })
    public ResponseEntity<Object> handleForbiddenException(Exception e, WebRequest request) {
        logger.error(e.getMessage());
        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    public ResponseEntity<Object> handleException(Exception e) {
        logger.error(e.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

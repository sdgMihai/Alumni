package com.licentamihai.alumni.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler {

    @ExceptionHandler(value = { NoHandlerFoundException.class })
    public ResponseEntity<Object> noHandlerFoundException(Exception ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("test");
    }
}
package com.iseage.wms;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleConflicts(DataIntegrityViolationException ex){
        String error = ex.getMessage().toLowerCase();
        if(error.contains("null")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required fields.");
        } else if(error.contains("unique") || error.contains("duplicate")){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Required fields should be unique!");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleJsonError(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body("Missing required fields.");
    }
}
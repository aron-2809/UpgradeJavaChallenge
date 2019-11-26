package com.upgrade.upgradejavachallenge.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RecordNotFoundException.class)
    public final ResponseEntity<Object> handleResourceNotFoundException(RecordNotFoundException ex, WebRequest request) {
        List<String> messages = Arrays.asList(ex.getLocalizedMessage());
        ErrorResponse errorResponse = new ErrorResponse("Requested resource was not found.", messages);
        return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        List<String> messages = Arrays.asList(ex.getLocalizedMessage());
        ErrorResponse errorResponse = new ErrorResponse("Server has an encountered Error", messages);
        return new ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> messages = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            messages.add(error.getDefaultMessage());
        }
        ErrorResponse errorResponse = new ErrorResponse("Input Validation Unsuccessful.", messages);
        return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
    }
}

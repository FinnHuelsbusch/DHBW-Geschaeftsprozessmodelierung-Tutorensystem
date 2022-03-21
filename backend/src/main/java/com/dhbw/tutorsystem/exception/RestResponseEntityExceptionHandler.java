package com.dhbw.tutorsystem.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// In this class all the exception Handling is defined. If an Exception is thrown, it is handled here to return a proper error to the user. 
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    // catch all exceptions and put them into defined error object as JSON response
    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<TSExceptionResponse> handleConflict(Exception exception) {
        TSExceptionResponse response;
        // check if the exception is defined in this project or by spring
        if (exception instanceof TSBaseException) {
            // if it is defined in this project, cast it and build a proper response by reading all the needed values
            TSBaseException tsBaseException = (TSBaseException) exception;
            response = new TSExceptionResponse(LocalDateTime.now(), tsBaseException.getStatus().value(),
                    tsBaseException.getErrorCode(), tsBaseException.getMessage());
        } else {
            // unknown exception, do not give too many details
            // All unknown errors are returned as internal server error
            logger.error("Unknown exception", exception);
            response = new TSExceptionResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    TSErrorCode.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
        // return the built error message by putting it into a response entity
        return new ResponseEntity<TSExceptionResponse>(response, HttpStatus.valueOf(response.getStatus()));
    }

    // override handling of javax.validation errors
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder sb = new StringBuilder();
        // for all exceptions write the field name and the error and append it to the string builder 
        ex.getBindingResult().getFieldErrors().stream().forEach(fieldError -> {
            sb.append(String.format("Field '%s' has error: %s. ", fieldError.getField(),
                    fieldError.getDefaultMessage()));
        });
        TSExceptionResponse response = new TSExceptionResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                TSErrorCode.BAD_REQUEST, sb.toString());
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

}

package com.dhbw.tutorsystem.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    // catch all exceptions and put them into defined error object as JSON response
    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<TSExceptionResponse> handleConflict(Exception exception) {
        TSExceptionResponse response;
        if (exception instanceof TSBaseException) {
            TSBaseException tsBaseException = (TSBaseException) exception;
            response = new TSExceptionResponse(LocalDateTime.now(), tsBaseException.getStatus().value(),
                    tsBaseException.getErrorCode(), tsBaseException.getMessage());
        } else {
            // unknown exception, do not give too many details
            response = new TSExceptionResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    TSErrorCode.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
        return new ResponseEntity<TSExceptionResponse>(response, HttpStatus.valueOf(response.getStatus()));
    }

}

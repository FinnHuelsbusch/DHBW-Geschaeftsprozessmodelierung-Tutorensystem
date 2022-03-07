package com.dhbw.tutorsystem.exception;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { TSBaseException.class })
    protected ResponseEntity<TSExceptionResponse> handleConflict(TSBaseException tsBaseException) {
        TSExceptionResponse response = new TSExceptionResponse(LocalDateTime.now(), tsBaseException.getStatus().value(),
                tsBaseException.getErrorCode(), tsBaseException.getMessage());
        return new ResponseEntity<TSExceptionResponse>(response, tsBaseException.getStatus());
    }

}

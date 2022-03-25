package com.dhbw.tutorsystem.exception;

import java.time.LocalDateTime;

import lombok.Data;


// basic exception class to be extended by all other exceptions
@Data
public class TSExceptionResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final TSErrorCode errorCode;
    private final String message;

}

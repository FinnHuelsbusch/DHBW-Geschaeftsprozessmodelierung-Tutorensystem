package com.dhbw.tutorsystem.exception;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TSExceptionResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final TSErrorCode errorCode;
    private final String message;

}

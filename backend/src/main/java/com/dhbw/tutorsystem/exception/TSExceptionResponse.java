package com.dhbw.tutorsystem.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TSExceptionResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final TSErrorCode errorCode;
    private final String message;

}

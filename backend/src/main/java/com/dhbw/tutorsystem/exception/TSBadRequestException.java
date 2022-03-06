package com.dhbw.tutorsystem.exception;

import org.springframework.http.HttpStatus;

public class TSBadRequestException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.BAD_REQUEST;

    public TSBadRequestException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "Error in the Request.");
    }

    public TSBadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }
}

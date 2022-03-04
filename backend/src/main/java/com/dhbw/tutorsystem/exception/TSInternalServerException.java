package com.dhbw.tutorsystem.exception;

import org.springframework.http.HttpStatus;

public class TSInternalServerException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.INTERNAL_SERVER_ERROR;

    public TSInternalServerException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, errorCode, "Error in internal server processing.");
    }

    public TSInternalServerException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, errorCode, message);
    }
}

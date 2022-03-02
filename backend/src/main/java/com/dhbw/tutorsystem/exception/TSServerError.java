package com.dhbw.tutorsystem.exception;

import org.springframework.http.HttpStatus;

public class TSServerError extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.INTERNAL_SERVER_ERROR;

    public TSServerError() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, errorCode, "Error in internal server processing.");
    }

    public TSServerError(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, errorCode, message);
    }
}

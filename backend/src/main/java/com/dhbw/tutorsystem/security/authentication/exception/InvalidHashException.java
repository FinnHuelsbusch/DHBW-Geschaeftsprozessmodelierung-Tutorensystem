package com.dhbw.tutorsystem.security.authentication.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class InvalidHashException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.INVALID_HASH;

    public InvalidHashException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "Supplied hash value is invalid.");
    }

    public InvalidHashException(String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }

}
